package ganadii.hanjum.service;

import ganadii.hanjum.domain.User;
import ganadii.hanjum.repository.UserRepository;
import ganadii.hanjum.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public User signup(String email, String rawPassword, String nickname) {
        userRepository.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("Email already in use");
        });
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .nickname(nickname)
                .build();
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public TokenPair issueTokens(User user) {
        String access = jwtTokenProvider.createAccessToken(user.getUserId(), user.getEmail());
        String refresh = jwtTokenProvider.createRefreshToken(user.getUserId(), user.getEmail());
        return new TokenPair(access, refresh);
    }

    @Transactional(readOnly = true)
    public TokenPair refresh(String refreshToken) {
        var claims = jwtTokenProvider.parse(refreshToken);
        Object typ = claims.get("typ");
        if (typ == null || !"refresh".equals(typ.toString())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        UUID userId = UUID.fromString(claims.getSubject());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return issueTokens(user);
    }

    public record TokenPair(String accessToken, String refreshToken) {}
}

