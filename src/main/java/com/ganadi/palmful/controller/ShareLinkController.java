package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.BouquetResponse;
import com.ganadi.palmful.service.ShareLinkService;
import com.ganadi.palmful.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "공유 링크", description = "부케 공유 링크 생성/조회 API")
@SecurityRequirement(name = "bearerAuth")
public class ShareLinkController {

    private final ShareLinkService shareLinkService;
    private final CurrentUserService currentUserService;

    @Autowired
    public ShareLinkController(ShareLinkService shareLinkService, CurrentUserService currentUserService) {
        this.shareLinkService = shareLinkService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/api/bouquets/{id}/share")
    @Operation(summary = "공유 링크 생성", description = "부케에 대한 공유 링크를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 완료"),
            @ApiResponse(responseCode = "404", description = "부케 없음")
    })
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

    @GetMapping("/api/share/{token}")
    @Operation(summary = "공유 링크로 부케 조회", description = "토큰으로 부케 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "부케 반환"),
            @ApiResponse(responseCode = "404", description = "링크 없음")
    })
    public ResponseEntity<BouquetResponse> getByToken(@PathVariable("token") String token) {
        try {
            return ResponseEntity.ok(shareLinkService.getByToken(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}



