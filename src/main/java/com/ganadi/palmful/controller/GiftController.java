package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.GiftRequest;
import com.ganadi.palmful.dto.GiftResponse;
import com.ganadi.palmful.service.GiftService;
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

import java.util.List;

@RestController
@RequestMapping("/api/gifts")
@Tag(name = "선물", description = "선물 보내기/조회 API")
@SecurityRequirement(name = "bearerAuth")
public class GiftController {

    private final GiftService giftService;

    @Autowired
    public GiftController(GiftService giftService) {
        this.giftService = giftService;
    }

    @PostMapping
    @Operation(summary = "선물 보내기", description = "부케를 다른 사용자에게 선물합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "전송 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<GiftResponse> sendGift(@Valid @RequestBody GiftRequest request) {
        try {
            // TODO: JWT에서 현재 사용자 ID 추출
            Long senderId = 1L; // 임시
            GiftResponse response = giftService.sendGift(senderId, request.getBouquetId(), request.getReceiverId(), request.getMessage());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/received")
    @Operation(summary = "받은 선물 목록", description = "현재 사용자가 받은 선물 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목록 반환")
    })
    public ResponseEntity<List<GiftResponse>> getReceived() {
        // TODO: JWT에서 현재 사용자 ID 추출
        Long userId = 1L; // 임시
        return ResponseEntity.ok(giftService.getReceivedGifts(userId));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "읽음 표시", description = "선물을 읽음 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "처리 완료"),
            @ApiResponse(responseCode = "404", description = "제공된 ID 없음")
    })
    public ResponseEntity<Void> markAsRead(@PathVariable("id") Long id) {
        try {
            giftService.markAsRead(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
