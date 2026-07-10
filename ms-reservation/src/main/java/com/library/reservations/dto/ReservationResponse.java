package com.library.reservations.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.library.reservations.entity.ReservationStatus;

public record ReservationResponse(
        UUID id,
        UUID userId,
        UUID bookId,
        LocalDate reservationDate,
        ReservationStatus status
) {
}