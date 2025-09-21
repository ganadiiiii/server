package ganadii.hanjum.controller;

import ganadii.hanjum.domain.User;
import ganadii.hanjum.repository.UserRepository;
import ganadii.hanjum.security.JwtTokenProvider;
import ganadii.hanjum.service.AuthService;
import ganadii.hanjum.dto.AuthDtos;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<AuthDtos.AuthResponse> signup(@Valid @RequestBody AuthDtos.SignupRequest req) {
        User user = authService.signup(req.email(), req.password(), req.nickname());
        var tokens = authService.issueTokens(user);
        return ResponseEntity.ok(new AuthDtos.AuthResponse(tokens.accessToken(), tokens.refreshToken(),
                new AuthDtos.UserResponse(user.getUserId(), user.getEmail(), user.getNickname())));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest req) {
        User user = authService.login(req.email(), req.password());
        var tokens = authService.issueTokens(user);
        return ResponseEntity.ok(new AuthDtos.AuthResponse(tokens.accessToken(), tokens.refreshToken(),
                new AuthDtos.UserResponse(user.getUserId(), user.getEmail(), user.getNickname())));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDtos.AuthResponse> refresh(@Valid @RequestBody AuthDtos.RefreshRequest req) {
        var tokens = authService.refresh(req.refreshToken());
        var claims = jwtTokenProvider.parse(req.refreshToken());
        UUID userId = UUID.fromString(claims.getSubject());
        User user = userRepository.findById(userId).orElse(null);
        var userResp = (user == null) ? null : new AuthDtos.UserResponse(user.getUserId(), user.getEmail(), user.getNickname());
        return ResponseEntity.ok(new AuthDtos.AuthResponse(tokens.accessToken(), tokens.refreshToken(), userResp));
    }
}
