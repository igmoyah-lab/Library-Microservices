package com.library.bff.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.FineResponse;
import com.library.bff.dto.LoanResponse;
import com.library.bff.dto.NotificationResponse;
import com.library.bff.dto.ReservationResponse;
import com.library.bff.dto.UserDashboardResponse;

@Service
public class DashboardService {

    private final LoanService loanService;
    private final ReservationService reservationService;
    private final FineService fineService;
    private final NotificationService notificationService;

    public DashboardService(
            LoanService loanService,
            ReservationService reservationService,
            FineService fineService,
            NotificationService notificationService
    ) {
        this.loanService = loanService;
        this.reservationService = reservationService;
        this.fineService = fineService;
        this.notificationService = notificationService;
    }

    /**
     * Construye un dashboard combinando información
     * de préstamos, reservas, multas y notificaciones.
     *
     * @param userId identificador del usuario
     * @return respuesta compuesta con la información del usuario
     */
    public ApiResponse<UserDashboardResponse> getUserDashboard(
            UUID userId
    ) {
        List<LoanResponse> loans =
                getLoansSafely(userId);

        List<ReservationResponse> reservations =
                getReservationsSafely(userId);

        List<FineResponse> fines =
                getFinesSafely(userId);

        List<NotificationResponse> notifications =
                getNotificationsSafely(userId);

        long unreadNotifications =
                getUnreadCountSafely(userId);

        long activeLoans = loans.stream()
                .filter(loan ->
                        "ACTIVE".equalsIgnoreCase(
                                loan.status()
                        )
                )
                .count();

        long activeReservations = reservations.stream()
                .filter(reservation ->
                        "ACTIVE".equalsIgnoreCase(
                                reservation.status()
                        )
                )
                .count();

        long pendingFines = fines.stream()
                .filter(fine ->
                        "PENDING".equalsIgnoreCase(
                                fine.status()
                        )
                )
                .count();

        UserDashboardResponse dashboard =
                new UserDashboardResponse(
                        userId,

                        loans,
                        reservations,
                        fines,
                        notifications,

                        loans.size(),
                        activeLoans,

                        reservations.size(),
                        activeReservations,

                        fines.size(),
                        pendingFines,

                        unreadNotifications
                );

        return new ApiResponse<>(
                true,
                dashboard,
                "Dashboard del usuario obtenido con éxito"
        );
    }

    /**
     * Obtiene los préstamos del usuario.
     * Si no existen, devuelve una lista vacía.
     */
    private List<LoanResponse> getLoansSafely(
            UUID userId
    ) {
        try {
            ApiResponse<List<LoanResponse>> response =
                    loanService.getLoansByUserId(userId);

            if (response == null || response.data() == null) {
                return List.of();
            }

            return response.data();

        } catch (HttpClientErrorException.NotFound exception) {
            return List.of();
        }
    }

    /**
     * Obtiene las reservas del usuario.
     * Si no existen, devuelve una lista vacía.
     */
    private List<ReservationResponse> getReservationsSafely(
            UUID userId
    ) {
        try {
            ApiResponse<List<ReservationResponse>> response =
                    reservationService
                            .getReservationsByUserId(userId);

            if (response == null || response.data() == null) {
                return List.of();
            }

            return response.data();

        } catch (HttpClientErrorException.NotFound exception) {
            return List.of();
        }
    }

    /**
     * Obtiene las multas del usuario.
     * Si no existen, devuelve una lista vacía.
     */
    private List<FineResponse> getFinesSafely(
            UUID userId
    ) {
        try {
            ApiResponse<List<FineResponse>> response =
                    fineService.getFinesByUserId(userId);

            if (response == null || response.data() == null) {
                return List.of();
            }

            return response.data();

        } catch (HttpClientErrorException.NotFound exception) {
            return List.of();
        }
    }

    /**
     * Obtiene las notificaciones del usuario.
     * Si no existen, devuelve una lista vacía.
     */
    private List<NotificationResponse>
            getNotificationsSafely(
                    UUID userId
            ) {
        try {
            ApiResponse<List<NotificationResponse>> response =
                    notificationService
                            .getNotificationsByUserId(userId);

            if (response == null || response.data() == null) {
                return List.of();
            }

            return response.data();

        } catch (HttpClientErrorException.NotFound exception) {
            return List.of();
        }
    }

    /**
     * Obtiene el número de notificaciones no leídas.
     * Si no hay información, devuelve cero.
     */
    private long getUnreadCountSafely(UUID userId) {
        try {
            ApiResponse<Long> response =
                    notificationService
                            .countUnreadNotifications(userId);

            if (response == null || response.data() == null) {
                return 0L;
            }

            return response.data();

        } catch (HttpClientErrorException.NotFound exception) {
            return 0L;
        }
    }
}
