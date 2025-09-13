package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.BouquetRequest;
import com.ganadi.palmful.dto.BouquetResponse;
import com.ganadi.palmful.service.BouquetService;
import com.ganadi.palmful.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bouquets")
@Tag(name = "부케", description = "부케 생성, 조회, 수정, 삭제 API")
@SecurityRequirement(name = "bearerAuth")
public class BouquetController {
    
    private final BouquetService bouquetService;
    private final CurrentUserService currentUserService;
    
    @Autowired
    public BouquetController(BouquetService bouquetService, CurrentUserService currentUserService) {
        this.bouquetService = bouquetService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "꽃다발 생성", description = "새로운 꽃다발을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "꽃다발 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    public ResponseEntity<BouquetResponse> createBouquet(@Valid @RequestBody BouquetRequest request) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            BouquetResponse response = bouquetService.createBouquet(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "꽃다발 상세 조회", description = "특정 꽃다발의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "꽃다발 상세 반환"),
        @ApiResponse(responseCode = "404", description = "없음")
    })
    public ResponseEntity<BouquetResponse> getBouquet(@PathVariable Long id) {
        try {
            BouquetResponse response = bouquetService.getBouquet(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "꽃다발 수정", description = "기존 꽃다발의 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 완료"),
        @ApiResponse(responseCode = "404", description = "없음"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ResponseEntity<BouquetResponse> updateBouquet(@PathVariable Long id, @Valid @RequestBody BouquetRequest request) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            BouquetResponse response = bouquetService.updateBouquet(id, userId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "꽃다발 삭제", description = "꽃다발을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "없음"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ResponseEntity<Void> deleteBouquet(@PathVariable Long id) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            bouquetService.deleteBouquet(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "꽃다발 아카이브", description = "꽃다발을 아카이브합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "아카이브 성공"),
        @ApiResponse(responseCode = "404", description = "없음"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ResponseEntity<BouquetResponse> archiveBouquet(@PathVariable Long id) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            BouquetResponse response = bouquetService.archiveBouquet(id, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/users/me/bouquets")
    @Operation(summary = "내 꽃다발 목록 조회", description = "현재 사용자의 꽃다발 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "꽃다발 목록 반환")
    })
    public ResponseEntity<java.util.List<BouquetResponse>> getMyBouquets(
            @RequestParam(value = "status", defaultValue = "all") String status) {
        Long userId = currentUserService.getCurrentUserId();
        java.util.List<BouquetResponse> bouquets = bouquetService.getUserBouquets(userId, status);
        return ResponseEntity.ok(bouquets);
    }



}
