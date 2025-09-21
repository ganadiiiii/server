package ganadii.hanjum.controller;

import ganadii.hanjum.domain.User;
import ganadii.hanjum.dto.AuthDtos;
import ganadii.hanjum.repository.UserRepository;
import ganadii.hanjum.web.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "02-사용자", description = "사용자 정보")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 기본 정보를 반환합니다.")
    public ResponseEntity<AuthDtos.UserResponse> me() {
        UUID userId = SecurityUtils.currentUserIdOrThrow();
        User user = userRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(new AuthDtos.UserResponse(user.getUserId(), user.getEmail(), user.getNickname()));
    }
}
