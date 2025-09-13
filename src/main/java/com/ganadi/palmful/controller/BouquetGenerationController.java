package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.BouquetResponse;
import com.ganadi.palmful.dto.GenerationRequest;
import com.ganadi.palmful.dto.GenerationResponse;
import com.ganadi.palmful.service.BouquetGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bouquets")
@Tag(name = "부케 생성 이력", description = "AI 생성/이력/발행 API")
@SecurityRequirement(name = "bearerAuth")
public class BouquetGenerationController {

    private final BouquetGenerationService generationService;

    @Autowired
    public BouquetGenerationController(BouquetGenerationService generationService) {
        this.generationService = generationService;
    }

    @PostMapping("/{id}/generate")
    @Operation(summary = "AI 미리보기 생성", description = "새로운 생성 버전을 만들어 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 완료"),
            @ApiResponse(responseCode = "404", description = "부케 없음")
    })
    public ResponseEntity<GenerationResponse> generate(@PathVariable("id") Long bouquetId,
                                                       @RequestBody(required = false) GenerationRequest request) {
        try {
            GenerationResponse response = generationService.generate(bouquetId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/generations")
    @Operation(summary = "생성 이력 조회", description = "해당 꽃다발의 생성 이력을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이력 반환"),
            @ApiResponse(responseCode = "404", description = "부케 없음")
    })
    public ResponseEntity<List<GenerationResponse>> getGenerations(@PathVariable("id") Long bouquetId) {
        try {
            return ResponseEntity.ok(generationService.getGenerations(bouquetId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "생성 버전 발행", description = "선택된 생성 버전을 부케 미리보기로 확정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발행 완료"),
            @ApiResponse(responseCode = "404", description = "부케 또는 생성 이력 없음"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    public ResponseEntity<BouquetResponse> publish(@PathVariable("id") Long bouquetId,
                                                   @RequestParam("generationId") Long generationId) {
        try {
            // TODO: 실제로는 JWT에서 사용자 ID를 추출해서 소유권 검증해야 함
            // 소유권 검증은 Service의 publish 전에 선행되어야 하지만, 예제에서는 간소화
            BouquetResponse response = generationService.publish(bouquetId, generationId);
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


