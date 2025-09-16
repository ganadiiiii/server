package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.GiftRequest;
import com.ganadi.palmful.dto.GiftResponse;
import com.ganadi.palmful.service.GiftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gifts")
@Tag(name = "선물", description = "선물 보내기 및 받은 선물 조회")
public class GiftController {

    private final GiftService giftService;
    private final com.ganadi.palmful.service.CurrentUserService currentUserService;

    @Autowired
    public GiftController(GiftService giftService, com.ganadi.palmful.service.CurrentUserService currentUserService) {
        this.giftService = giftService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "선물 보내기", description = "친구에게 꽃다발을 선물로 보냅니다.")
    public ResponseEntity<GiftResponse> sendGift(@Valid @RequestBody GiftRequest request) {
        try {
            Long senderId = currentUserService.getCurrentUserId();
            GiftResponse response = giftService.sendGift(senderId, request.getBouquetId(), request.getReceiverId(), request.getMessage());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/received")
    @Operation(summary = "받은 선물 조회", description = "내가 받은 선물 목록을 조회합니다.")
    public ResponseEntity<List<GiftResponse>> getReceived() {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(giftService.getReceivedGifts(userId));
    }

    @GetMapping("/sent")
    @Operation(summary = "보낸 선물 조회", description = "내가 보낸 선물 목록을 조회합니다.")
    public ResponseEntity<List<GiftResponse>> getSent() {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(giftService.getSentGifts(userId));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "선물 읽음 처리", description = "받은 선물을 읽음으로 표시합니다.")
    public ResponseEntity<Void> markAsRead(@PathVariable("id") Long id) {
        try {
            giftService.markAsRead(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
