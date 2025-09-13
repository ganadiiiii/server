package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.FlowerResponse;
import com.ganadi.palmful.service.FlowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flowers")
@Tag(name = "꽃", description = "꽃 정보 조회 API")
public class FlowerController {

    private final FlowerService flowerService;

    @Autowired
    public FlowerController(FlowerService flowerService) {
        this.flowerService = flowerService;
    }

    @GetMapping
    @Operation(summary = "꽃 목록 조회", description = "전체 꽃 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "꽃 목록 반환")
    })
    public ResponseEntity<Page<FlowerResponse>> getFlowers(Pageable pageable) {
        List<FlowerResponse> items = flowerService.getAllFlowers(pageable);
        Page<FlowerResponse> page = new PageImpl<>(items, pageable, items.size());
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "단일 꽃 상세 조회", description = "특정 꽃의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "꽃 상세 반환"),
        @ApiResponse(responseCode = "404", description = "없음")
    })
    public ResponseEntity<FlowerResponse> getFlower(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(flowerService.getFlower(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
