package ganadii.hanjum.web;

import ganadii.hanjum.domain.User;
import ganadii.hanjum.repository.UserRepository;
import ganadii.hanjum.web.dto.AuthDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<AuthDtos.UserResponse> me() {
        UUID userId = SecurityUtils.currentUserIdOrThrow();
        User user = userRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(new AuthDtos.UserResponse(user.getUserId(), user.getEmail(), user.getNickname()));
    }
}

