package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "선물 정보 응답")
public class GiftResponse {

    @Schema(description = "선물 ID", example = "1")
    private Long id;

    @Schema(description = "보낸 사람")
    private UserResponse sender;

    @Schema(description = "받는 사람")
    private UserResponse receiver;

    @Schema(description = "부케 ID", example = "10")
    private Long bouquetId;

    @Schema(description = "메시지", example = "생일 축하해!")
    private String message;

    @Schema(description = "상태", example = "sent")
    private String status;

    @Schema(description = "보낸 시각")
    private LocalDateTime sentAt;

    public GiftResponse() {}

    public GiftResponse(Long id, UserResponse sender, UserResponse receiver, Long bouquetId, String message, String status, LocalDateTime sentAt) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.bouquetId = bouquetId;
        this.message = message;
        this.status = status;
        this.sentAt = sentAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserResponse getSender() { return sender; }
    public void setSender(UserResponse sender) { this.sender = sender; }
    public UserResponse getReceiver() { return receiver; }
    public void setReceiver(UserResponse receiver) { this.receiver = receiver; }
    public Long getBouquetId() { return bouquetId; }
    public void setBouquetId(Long bouquetId) { this.bouquetId = bouquetId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}



