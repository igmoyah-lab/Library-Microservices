package com.library.bff.dto;

import java.util.List;
import java.util.UUID;

public record UserDashboardResponse(
        UUID userId,

        List<LoanResponse> loans,
        List<ReservationResponse> reservations,
        List<FineResponse> fines,
        List<NotificationResponse> notifications,

        long totalLoans,
        long activeLoans,

        long totalReservations,
        long activeReservations,

        long totalFines,
        long pendingFines,

        long unreadNotifications
) {
}
