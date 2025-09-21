package ganadii.hanjum.controller;

import ganadii.hanjum.domain.User;
import ganadii.hanjum.dto.AuthDtos;
import ganadii.hanjum.repository.UserRepository;
import ganadii.hanjum.security.JwtTokenProvider;
import ganadii.hanjum.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "01-인증", description = "로그인 및 토큰 재발급")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "이메일과 닉네임으로 신규 사용자를 등록합니다.")
    public ResponseEntity<AuthDtos.AuthResponse> signup(@Valid @RequestBody AuthDtos.SignupRequest req) {
        User user = authService.signup(req.email(), req.password(), req.nickname());
        var tokens = authService.issueTokens(user);
        return ResponseEntity.ok(new AuthDtos.AuthResponse(tokens.accessToken(), tokens.refreshToken(),
                new AuthDtos.UserResponse(user.getUserId(), user.getEmail(), user.getNickname())));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 JWT를 발급합니다.")
    public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest req) {
        User user = authService.login(req.email(), req.password());
        var tokens = authService.issueTokens(user);
        return ResponseEntity.ok(new AuthDtos.AuthResponse(tokens.accessToken(), tokens.refreshToken(),
                new AuthDtos.UserResponse(user.getUserId(), user.getEmail(), user.getNickname())));
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 Access Token을 재발급합니다.")
    public ResponseEntity<AuthDtos.AuthResponse> refresh(@Valid @RequestBody AuthDtos.RefreshRequest req) {
        var tokens = authService.refresh(req.refreshToken());
        var claims = jwtTokenProvider.parse(req.refreshToken());
        UUID userId = UUID.fromString(claims.getSubject());
        User user = userRepository.findById(userId).orElse(null);
        var userResp = (user == null) ? null : new AuthDtos.UserResponse(user.getUserId(), user.getEmail(), user.getNickname());
        return ResponseEntity.ok(new AuthDtos.AuthResponse(tokens.accessToken(), tokens.refreshToken(), userResp));
    }
}
