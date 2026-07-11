package com.library.bff.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        UUID userId,
        UUID bookId,
        LocalDate reservationDate,
        String status
) {
}