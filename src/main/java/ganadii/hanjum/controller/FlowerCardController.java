package ganadii.hanjum.controller;

import ganadii.hanjum.dto.FlowerCardDtos;
import ganadii.hanjum.service.FlowerCardService;
import ganadii.hanjum.web.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class FlowerCardController {

    private final FlowerCardService flowerCardService;

    @PostMapping
    public ResponseEntity<FlowerCardDtos.CardResponse> createCard(
            @Valid @RequestBody FlowerCardDtos.CreateCardRequest request,
            @RequestHeader(name = "X-User-Id", required = false) String userHeader
    ) {
        UUID userId = resolveUserId(userHeader);
        FlowerCardDtos.CardResponse response = flowerCardService.createCard(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private UUID resolveUserId(String userHeader) {
        if (userHeader != null && !userHeader.isBlank()) {
            return UUID.fromString(userHeader.trim());
        }
        return SecurityUtils.currentUserIdOrThrow();
    }
}
