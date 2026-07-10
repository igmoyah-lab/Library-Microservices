package com.library.notifications.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        String message,
        LocalDateTime createdAt
) {
}