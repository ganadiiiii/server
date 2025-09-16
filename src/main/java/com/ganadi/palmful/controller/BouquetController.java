package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.BouquetRequest;
import com.ganadi.palmful.dto.BouquetResponse;
import com.ganadi.palmful.service.BouquetService;
import com.ganadi.palmful.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bouquets")
@Tag(name = "부케", description = "부케 생성/조회/수정/삭제 및 아카이브")
public class BouquetController {
    
    private final BouquetService bouquetService;
    private final CurrentUserService currentUserService;
    
    @Autowired
    public BouquetController(BouquetService bouquetService, CurrentUserService currentUserService) {
        this.bouquetService = bouquetService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "부케 생성", description = "요청 본문으로 부케를 생성합니다.")
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
    @Operation(summary = "부케 단건 조회", description = "부케 ID로 상세 정보를 조회합니다.")
    public ResponseEntity<BouquetResponse> getBouquet(@PathVariable Long id) {
        try {
            BouquetResponse response = bouquetService.getBouquet(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "부케 수정", description = "부케 정보를 수정합니다. 소유자만 가능합니다.")
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
    @Operation(summary = "부케 삭제", description = "부케를 삭제합니다. 소유자만 가능합니다.")
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
    @Operation(summary = "부케 아카이브", description = "부케 상태를 archived로 변경합니다.")
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
    @Operation(summary = "내 부케 목록", description = "상태 필터(active/archived/all)로 내 부케를 조회합니다.")
    public ResponseEntity<java.util.List<BouquetResponse>> getMyBouquets(
            @RequestParam(value = "status", defaultValue = "all") String status) {
        Long userId = currentUserService.getCurrentUserId();
        java.util.List<BouquetResponse> bouquets = bouquetService.getUserBouquets(userId, status);
        return ResponseEntity.ok(bouquets);
    }



}
