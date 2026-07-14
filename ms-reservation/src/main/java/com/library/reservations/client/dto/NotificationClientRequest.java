package com.library.reservations.client.dto;

import java.util.UUID;

public record NotificationClientRequest(
        UUID userId,
        String message
) {
}