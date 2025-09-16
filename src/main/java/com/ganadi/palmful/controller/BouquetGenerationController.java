package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.BouquetResponse;
import com.ganadi.palmful.dto.GenerationRequest;
import com.ganadi.palmful.dto.GenerationResponse;
import com.ganadi.palmful.service.BouquetGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bouquets")
@Tag(name = "부케 생성", description = "AI를 이용한 꽃다발 생성 및 관리")
public class BouquetGenerationController {

    private final BouquetGenerationService generationService;
    private final com.ganadi.palmful.service.CurrentUserService currentUserService;

    @Autowired
    public BouquetGenerationController(BouquetGenerationService generationService, com.ganadi.palmful.service.CurrentUserService currentUserService) {
        this.generationService = generationService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/{id}/generate")
    @Operation(summary = "꽃다발 생성", description = "AI를 이용해 꽃다발을 생성합니다.")
    public ResponseEntity<GenerationResponse> generate(@PathVariable("id") Long bouquetId,
                                                       @RequestBody(required = false) GenerationRequest request) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            GenerationResponse response = generationService.generate(userId, bouquetId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/generations")
    @Operation(summary = "생성 이력 조회", description = "특정 부케의 생성 이력을 조회합니다.")
    public ResponseEntity<List<GenerationResponse>> getGenerations(@PathVariable("id") Long bouquetId) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            return ResponseEntity.ok(generationService.getGenerations(userId, bouquetId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "부케 발행", description = "생성된 꽃다발을 발행합니다.")
    public ResponseEntity<BouquetResponse> publish(@PathVariable("id") Long bouquetId,
                                                   @RequestParam("generationId") Long generationId) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            BouquetResponse response = generationService.publish(userId, bouquetId, generationId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.contains("권한") || msg.contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}


