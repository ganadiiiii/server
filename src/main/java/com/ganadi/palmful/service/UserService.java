package com.ganadi.palmful.service;

import com.ganadi.palmful.dto.AuthResponse;
import com.ganadi.palmful.dto.LoginRequest;
import com.ganadi.palmful.dto.RegisterRequest;
import com.ganadi.palmful.dto.UserResponse;
import com.ganadi.palmful.entity.User;
import com.ganadi.palmful.repository.UserRepository;
import com.ganadi.palmful.config.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    
    /**
     * 사용자 회원가입
     * @param request 회원가입 요청 정보
     * @return 사용자 정보 응답
     * @throws IllegalArgumentException 이메일 중복 시
     */
    public UserResponse registerUser(RegisterRequest request) {
        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        
        // User 엔티티 생성
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setProvider("local");
        user.setPasswordHash(hashedPassword);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // 데이터베이스에 저장
        User savedUser;
        try {
            savedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            // 이메일 UNIQUE 위반 시 전역 핸들러가 409로 응답하도록 그대로 전파
            throw ex;
        }
        
        // UserResponse로 변환하여 반환
        return convertToUserResponse(savedUser);
    }
    
    /**
     * 사용자 로그인
     * @param request 로그인 요청 정보
     * @return 인증 응답 (JWT 토큰 포함)
     * @throws IllegalArgumentException 인증 실패 시
     */
    public AuthResponse loginUser(LoginRequest request) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        
        // JWT 토큰 생성
        String token = jwtService.generateToken(user.getEmail(), java.util.Map.of("uid", user.getId()));
        
        // UserResponse 생성
        UserResponse userResponse = convertToUserResponse(user);
        
        // AuthResponse 생성 및 반환
        return new AuthResponse(token, "Bearer", userResponse);
    }
    
    /**
     * 이메일로 사용자 조회
     * @param email 조회할 이메일
     * @return 사용자 정보 응답
     * @throws IllegalArgumentException 사용자를 찾을 수 없을 때
     */
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
        
        return convertToUserResponse(user);
    }
    
    /**
     * User 엔티티를 UserResponse로 변환
     * @param user 변환할 User 엔티티
     * @return UserResponse
     */
    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getProvider(),
                user.getCreatedAt()
        );
    }
}
