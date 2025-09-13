package com.ganadi.palmful.service;

import com.ganadi.palmful.entity.User;
import com.ganadi.palmful.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    @Autowired
    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 현재 인증된 사용자의 ID를 반환
     * @return 사용자 ID
     * @throws IllegalArgumentException 인증 정보가 없거나 사용자를 찾을 수 없을 때
     */
    public Long getCurrentUserId() {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("현재 사용자를 찾을 수 없습니다: " + email));
        return user.getId();
    }

    /**
     * 현재 인증된 사용자의 이메일을 반환
     * @return 사용자 이메일
     * @throws IllegalArgumentException 인증 정보가 없을 때
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }
        return authentication.getName();
    }

    /**
     * 현재 인증된 사용자 엔티티를 반환
     * @return User 엔티티
     * @throws IllegalArgumentException 인증 정보가 없거나 사용자를 찾을 수 없을 때
     */
    public User getCurrentUser() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("현재 사용자를 찾을 수 없습니다: " + email));
    }
}
