package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.NotificationResponse;
import com.ganadi.palmful.service.NotificationService;
import com.ganadi.palmful.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "알림", description = "알림 조회 및 읽음 처리")
public class NotificationController {
    
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;
    
    @Autowired
    public NotificationController(NotificationService notificationService, CurrentUserService currentUserService) {
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }
    
    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "내 알림 목록을 조회합니다.")
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        try {
            Long userId = currentUserService.getCurrentUserId();
            List<NotificationResponse> notifications = notificationService.getUserNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/page")
    @Operation(summary = "알림 목록 조회 (페이징)", description = "내 알림 목록을 페이징으로 조회합니다.")
    public ResponseEntity<Page<NotificationResponse>> getNotifications(Pageable pageable) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            Page<NotificationResponse> notifications = notificationService.getUserNotifications(userId, pageable);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/unread-count")
    @Operation(summary = "읽지 않은 알림 수", description = "읽지 않은 알림의 개수를 조회합니다.")
    public ResponseEntity<Long> getUnreadCount() {
        try {
            Long userId = currentUserService.getCurrentUserId();
            Long count = notificationService.getUnreadCount(userId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PatchMapping("/{id}/read")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음으로 표시합니다.")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            notificationService.markAsRead(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PatchMapping("/read-all")
    @Operation(summary = "모든 알림 읽음 처리", description = "모든 알림을 읽음으로 표시합니다.")
    public ResponseEntity<Void> markAllAsRead() {
        try {
            Long userId = currentUserService.getCurrentUserId();
            notificationService.markAllAsRead(userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
