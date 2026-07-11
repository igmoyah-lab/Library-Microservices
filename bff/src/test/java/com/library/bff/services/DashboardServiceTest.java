package com.library.bff.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.FineResponse;
import com.library.bff.dto.LoanResponse;
import com.library.bff.dto.NotificationResponse;
import com.library.bff.dto.ReservationResponse;
import com.library.bff.dto.UserDashboardResponse;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private LoanService loanService;

    @Mock
    private ReservationService reservationService;

    @Mock
    private FineService fineService;

    @Mock
    private NotificationService notificationService;

    private DashboardService createService() {
        return new DashboardService(
                loanService,
                reservationService,
                fineService,
                notificationService
        );
    }

    @Test
    void getUserDashboard_shouldCombineInformationFromServices() {
        DashboardService dashboardService =
                createService();

        UUID userId = UUID.randomUUID();

        List<LoanResponse> loans = List.of(
                new LoanResponse(
                        UUID.randomUUID(),
                        userId,
                        UUID.randomUUID(),
                        LocalDate.now(),
                        LocalDate.now().plusDays(7),
                        "ACTIVE"
                ),
                new LoanResponse(
                        UUID.randomUUID(),
                        userId,
                        UUID.randomUUID(),
                        LocalDate.now().minusDays(10),
                        LocalDate.now().minusDays(3),
                        "RETURNED"
                )
        );

        List<ReservationResponse> reservations = List.of(
                new ReservationResponse(
                        UUID.randomUUID(),
                        userId,
                        UUID.randomUUID(),
                        LocalDate.now(),
                        "ACTIVE"
                ),
                new ReservationResponse(
                        UUID.randomUUID(),
                        userId,
                        UUID.randomUUID(),
                        LocalDate.now().minusDays(2),
                        "CANCELLED"
                )
        );

        List<FineResponse> fines = List.of(
                new FineResponse(
                        UUID.randomUUID(),
                        userId,
                        UUID.randomUUID(),
                        BigDecimal.valueOf(3000),
                        LocalDate.now(),
                        "PENDING"
                ),
                new FineResponse(
                        UUID.randomUUID(),
                        userId,
                        UUID.randomUUID(),
                        BigDecimal.valueOf(2000),
                        LocalDate.now().minusDays(1),
                        "PAID"
                )
        );

        List<NotificationResponse> notifications = List.of(
                new NotificationResponse(
                        UUID.randomUUID(),
                        userId,
                        "Préstamo creado",
                        LocalDateTime.now(),
                        false
                ),
                new NotificationResponse(
                        UUID.randomUUID(),
                        userId,
                        "Reserva cancelada",
                        LocalDateTime.now().minusHours(1),
                        true
                )
        );

        when(loanService.getLoansByUserId(userId))
                .thenReturn(
                        new ApiResponse<>(
                                true,
                                loans,
                                "Préstamos obtenidos"
                        )
                );

        when(
                reservationService
                        .getReservationsByUserId(userId)
        ).thenReturn(
                new ApiResponse<>(
                        true,
                        reservations,
                        "Reservas obtenidas"
                )
        );

        when(fineService.getFinesByUserId(userId))
                .thenReturn(
                        new ApiResponse<>(
                                true,
                                fines,
                                "Multas obtenidas"
                        )
                );

        when(
                notificationService
                        .getNotificationsByUserId(userId)
        ).thenReturn(
                new ApiResponse<>(
                        true,
                        notifications,
                        "Notificaciones obtenidas"
                )
        );

        when(
                notificationService
                        .countUnreadNotifications(userId)
        ).thenReturn(
                new ApiResponse<>(
                        true,
                        1L,
                        "Conteo obtenido"
                )
        );

        ApiResponse<UserDashboardResponse> result =
                dashboardService.getUserDashboard(userId);

        assertTrue(result.success());
        assertNotNull(result.data());

        assertEquals(userId, result.data().userId());

        assertEquals(2, result.data().totalLoans());
        assertEquals(1, result.data().activeLoans());

        assertEquals(
                2,
                result.data().totalReservations()
        );

        assertEquals(
                1,
                result.data().activeReservations()
        );

        assertEquals(2, result.data().totalFines());
        assertEquals(1, result.data().pendingFines());

        assertEquals(
                1,
                result.data().unreadNotifications()
        );

        assertEquals(2, result.data().loans().size());
        assertEquals(
                2,
                result.data().reservations().size()
        );
        assertEquals(2, result.data().fines().size());
        assertEquals(
                2,
                result.data().notifications().size()
        );

        assertEquals(
                "Dashboard del usuario obtenido con éxito",
                result.message()
        );

        verify(loanService).getLoansByUserId(userId);

        verify(reservationService)
                .getReservationsByUserId(userId);

        verify(fineService).getFinesByUserId(userId);

        verify(notificationService)
                .getNotificationsByUserId(userId);

        verify(notificationService)
                .countUnreadNotifications(userId);
    }

    @Test
    void getUserDashboard_shouldUseEmptyValuesWhenResponsesHaveNoData() {
        DashboardService dashboardService =
                createService();

        UUID userId = UUID.randomUUID();

        when(loanService.getLoansByUserId(userId))
                .thenReturn(
                        new ApiResponse<>(
                                true,
                                null,
                                "Sin préstamos"
                        )
                );

        when(
                reservationService
                        .getReservationsByUserId(userId)
        ).thenReturn(
                new ApiResponse<>(
                        true,
                        null,
                        "Sin reservas"
                )
        );

        when(fineService.getFinesByUserId(userId))
                .thenReturn(
                        new ApiResponse<>(
                                true,
                                null,
                                "Sin multas"
                        )
                );

        when(
                notificationService
                        .getNotificationsByUserId(userId)
        ).thenReturn(
                new ApiResponse<>(
                        true,
                        null,
                        "Sin notificaciones"
                )
        );

        when(
                notificationService
                        .countUnreadNotifications(userId)
        ).thenReturn(
                new ApiResponse<>(
                        true,
                        null,
                        "Sin conteo"
                )
        );

        ApiResponse<UserDashboardResponse> result =
                dashboardService.getUserDashboard(userId);

        assertTrue(result.success());
        assertNotNull(result.data());

        assertTrue(result.data().loans().isEmpty());
        assertTrue(result.data().reservations().isEmpty());
        assertTrue(result.data().fines().isEmpty());
        assertTrue(result.data().notifications().isEmpty());

        assertEquals(0, result.data().totalLoans());
        assertEquals(0, result.data().activeLoans());
        assertEquals(
                0,
                result.data().totalReservations()
        );
        assertEquals(
                0,
                result.data().activeReservations()
        );
        assertEquals(0, result.data().totalFines());
        assertEquals(0, result.data().pendingFines());
        assertEquals(
                0,
                result.data().unreadNotifications()
        );
    }

    @Test
    void getUserDashboard_shouldPropagateUnexpectedServiceFailure() {
        DashboardService dashboardService =
                createService();

        UUID userId = UUID.randomUUID();

        when(loanService.getLoansByUserId(userId))
                .thenThrow(
                        new IllegalStateException(
                                "ms-loan no disponible"
                        )
                );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> dashboardService
                        .getUserDashboard(userId)
        );

        assertEquals(
                "ms-loan no disponible",
                exception.getMessage()
        );
    }
}
