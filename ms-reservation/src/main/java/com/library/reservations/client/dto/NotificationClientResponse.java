package com.library.reservations.client.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationClientResponse(
        UUID id,
        UUID userId,
        String message,
        LocalDateTime createdAt
) {
}
