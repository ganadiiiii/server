package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.BouquetResponse;
import com.ganadi.palmful.service.ShareLinkService;
import com.ganadi.palmful.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "공유 링크", description = "부케 공유 링크 생성/조회")
public class ShareLinkController {

    private final ShareLinkService shareLinkService;
    private final CurrentUserService currentUserService;

    @Autowired
    public ShareLinkController(ShareLinkService shareLinkService, CurrentUserService currentUserService) {
        this.shareLinkService = shareLinkService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/bouquets/{id}/share")
    @Operation(summary = "공유 링크 생성", description = "부케를 공유하기 위한 토큰을 발급합니다.")
    public ResponseEntity<String> create(@PathVariable("id") Long bouquetId,
                                         @RequestParam(name = "channel", required = false) String channel) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            String token = shareLinkService.createLink(userId, bouquetId, channel);
            return ResponseEntity.status(HttpStatus.CREATED).body(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/share/{token}")
    @Operation(summary = "공유 보기", description = "토큰으로 공유된 부케 정보를 조회합니다.")
    public ResponseEntity<BouquetResponse> getByToken(@PathVariable("token") String token) {
        try {
            return ResponseEntity.ok(shareLinkService.getByToken(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}



