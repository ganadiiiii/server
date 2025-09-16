package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.FlowerResponse;
import com.ganadi.palmful.service.FlowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/flowers")
@Tag(name = "꽃", description = "꽃 목록 조회")
public class FlowerController {

    private final FlowerService flowerService;

    @Autowired
    public FlowerController(FlowerService flowerService) {
        this.flowerService = flowerService;
    }

    @GetMapping
    @Operation(summary = "꽃 목록 조회", description = "시스템에 등록된 모든 꽃 정보를 조회합니다.")
    public List<FlowerResponse> getFlowers() {
        return flowerService.getAllFlowers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "꽃 상세 조회", description = "특정 꽃의 상세 정보를 조회합니다.")
    public FlowerResponse getFlower(@PathVariable Long id) {
        return flowerService.getFlower(id);
    }
}