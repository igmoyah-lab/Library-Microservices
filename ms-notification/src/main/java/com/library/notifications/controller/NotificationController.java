package com.library.notifications.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.notifications.dto.ApiResponse;
import com.library.notifications.dto.NotificationRequest;
import com.library.notifications.dto.NotificationResponse;
import com.library.notifications.service.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>>
            createNotification(
                    @Valid @RequestBody
                    NotificationRequest notificationRequest
            ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        notificationService.createNotification(
                                notificationRequest
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>>
            getNotificationById(
                    @PathVariable UUID id
            ) {
        return ResponseEntity.ok(
                notificationService.getNotificationById(id)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<
            ApiResponse<List<NotificationResponse>>
    > getNotificationsByUserId(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                notificationService
                        .getNotificationsByUserId(userId)
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>>
            markAsRead(
                    @PathVariable UUID id
            ) {
        return ResponseEntity.ok(
                notificationService.markAsRead(id)
        );
    }

    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<ApiResponse<Long>>
            countUnreadNotifications(
                    @PathVariable UUID userId
            ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        notificationService
                                .countUnreadNotifications(userId),
                        "Cantidad de notificaciones no leídas obtenida con éxito"
                )
        );
    }
}