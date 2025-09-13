package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.*;
import com.ganadi.palmful.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증", description = "회원가입, 로그인, 소셜 로그인 API")
public class AuthController {
    
    private final UserService userService;
    
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    @Operation(summary = "회원가입", description = "이메일과 비밀번호로 회원가입을 진행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = com.ganadi.palmful.common.ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "이메일 중복", content = @Content(schema = @Schema(implementation = com.ganadi.palmful.common.ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse created = userService.registerUser(request);
        var location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/users/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 진행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공 (JWT 발급)", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = com.ganadi.palmful.common.ErrorResponse.class)))
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }
/* // 소셜 로그인 기능 추가 시 사용
    @PostMapping("/oauth/{provider}")
    @Operation(summary = "소셜 로그인", description = "Google, Kakao, GitHub 등 소셜 로그인을 진행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "JWT 토큰 반환"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<AuthResponse> oauthLogin(
            @PathVariable String provider,
            @Valid @RequestBody OAuthRequest request) {
        // TODO: 실제 소셜 로그인 로직 구현
        AuthResponse response = new AuthResponse();
        response.setToken("dummy-oauth-token");
        response.setUser(new UserResponse(1L, "user@example.com", "홍길동", "김", provider, null));
        
        return ResponseEntity.ok(response);
    }
*/
}
