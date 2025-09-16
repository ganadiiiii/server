package com.ganadi.palmful.dto;

import java.time.LocalDateTime;

public class GiftResponse {

    private Long id;

    private UserResponse sender;

    private UserResponse receiver;

    private Long bouquetId;

    private String message;

    private String status;

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



