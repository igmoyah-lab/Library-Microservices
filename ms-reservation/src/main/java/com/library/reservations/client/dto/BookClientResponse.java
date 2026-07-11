package com.library.reservations.client.dto;

import java.util.UUID;

public record BookClientResponse(
        UUID id,
        String title,
        String author,
        String category,
        String isbn,
        Boolean available
) {
}
