package com.ganadi.palmful.controller;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.ganadi.palmful.dto.AuthResponse;
import com.ganadi.palmful.dto.LoginRequest;
import com.ganadi.palmful.dto.RegisterRequest;
import com.ganadi.palmful.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증", description = "로그인/회원가입 등 인증 관련")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "인증 확인", description = "테스트용 엔드포인트")
    public String getAuth() {
        return "Hello Auth";
    }

    @PostMapping("/register")
    @Deprecated
    @Operation(summary = "회원가입(사용 중단)", description = "해당 엔드포인트는 사용 중단되었습니다. 대신 /api/auth/signup 을 사용하세요.", deprecated = true)
    public ResponseEntity<com.ganadi.palmful.dto.UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            var user = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "이메일/비밀번호로 회원가입을 수행합니다.")
    public ResponseEntity<com.ganadi.palmful.dto.UserResponse> signup(@Valid @RequestBody RegisterRequest request) {
        try {
            var user = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인하고 JWT를 발급합니다.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            var auth = userService.loginUser(request);
            return ResponseEntity.ok(auth);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}