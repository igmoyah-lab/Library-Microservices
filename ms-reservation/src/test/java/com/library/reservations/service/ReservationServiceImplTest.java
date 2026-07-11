package com.library.reservations.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.reservations.client.BookClient;
import com.library.reservations.client.NotificationClient;
import com.library.reservations.client.UserClient;
import com.library.reservations.client.dto.BookClientResponse;
import com.library.reservations.client.dto.NotificationClientRequest;
import com.library.reservations.client.dto.NotificationClientResponse;
import com.library.reservations.client.dto.UserClientResponse;
import com.library.reservations.dto.ApiResponse;
import com.library.reservations.dto.ReservationRequest;
import com.library.reservations.dto.ReservationResponse;
import com.library.reservations.entity.Reservation;
import com.library.reservations.entity.ReservationStatus;
import com.library.reservations.exception.BusinessRuleException;
import com.library.reservations.exception.DuplicateResourceException;
import com.library.reservations.exception.ExternalServiceException;
import com.library.reservations.exception.ResourceNotFoundException;
import com.library.reservations.repository.ReservationRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private BookClient bookClient;

    @Mock
    private NotificationClient notificationClient;

    private ReservationServiceImpl createService() {
        return new ReservationServiceImpl(
                reservationRepository,
                userClient,
                bookClient,
                notificationClient
        );
    }

    @Test
    void createReservation_shouldCreateReservationWhenBookIsUnavailable() {
        ReservationServiceImpl reservationService =
                createService();

        UUID reservationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        ReservationRequest request =
                new ReservationRequest(userId, bookId);

        when(userClient.getUserById(userId))
                .thenReturn(userResponse(userId));

        when(bookClient.getBookById(bookId))
                .thenReturn(
                        bookResponse(
                                bookId,
                                "Clean Code",
                                false
                        )
                );

        when(
                reservationRepository
                        .existsByUserIdAndBookIdAndStatus(
                                userId,
                                bookId,
                                ReservationStatus.ACTIVE
                        )
        ).thenReturn(false);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> {
                    Reservation reservation =
                            invocation.getArgument(0);

                    reservation.setId(reservationId);

                    return reservation;
                });

        when(
                notificationClient.createNotification(
                        any(NotificationClientRequest.class)
                )
        ).thenReturn(
                notificationResponse(userId)
        );

        ApiResponse<ReservationResponse> result =
                reservationService.createReservation(request);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(reservationId, result.data().id());
        assertEquals(userId, result.data().userId());
        assertEquals(bookId, result.data().bookId());
        assertEquals(
                ReservationStatus.ACTIVE,
                result.data().status()
        );
        assertEquals(
                "Reserva creada con éxito",
                result.message()
        );

        verify(userClient).getUserById(userId);
        verify(bookClient).getBookById(bookId);

        verify(reservationRepository).save(
                any(Reservation.class)
        );

        verify(notificationClient).createNotification(
                any(NotificationClientRequest.class)
        );
    }

    @Test
    void createReservation_shouldRejectAvailableBook() {
        ReservationServiceImpl reservationService =
                createService();

        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        ReservationRequest request =
                new ReservationRequest(userId, bookId);

        when(userClient.getUserById(userId))
                .thenReturn(userResponse(userId));

        when(bookClient.getBookById(bookId))
                .thenReturn(
                        bookResponse(
                                bookId,
                                "Spring Boot",
                                true
                        )
                );

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> reservationService.createReservation(
                        request
                )
        );

        assertEquals(
                "El libro se encuentra disponible y no necesita reserva",
                exception.getMessage()
        );

        verify(reservationRepository, never())
                .save(any(Reservation.class));

        verify(notificationClient, never())
                .createNotification(
                        any(NotificationClientRequest.class)
                );
    }

    @Test
    void createReservation_shouldRejectDuplicateActiveReservation() {
        ReservationServiceImpl reservationService =
                createService();

        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        ReservationRequest request =
                new ReservationRequest(userId, bookId);

        when(userClient.getUserById(userId))
                .thenReturn(userResponse(userId));

        when(bookClient.getBookById(bookId))
                .thenReturn(
                        bookResponse(
                                bookId,
                                "Java",
                                false
                        )
                );

        when(
                reservationRepository
                        .existsByUserIdAndBookIdAndStatus(
                                userId,
                                bookId,
                                ReservationStatus.ACTIVE
                        )
        ).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> reservationService.createReservation(
                        request
                )
        );

        assertEquals(
                "El usuario ya tiene una reserva activa para este libro",
                exception.getMessage()
        );

        verify(reservationRepository, never())
                .save(any(Reservation.class));

        verify(notificationClient, never())
                .createNotification(
                        any(NotificationClientRequest.class)
                );
    }

    @Test
    void createReservation_shouldRollbackWhenNotificationFails() {
        ReservationServiceImpl reservationService =
                createService();

        UUID reservationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        ReservationRequest request =
                new ReservationRequest(userId, bookId);

        when(userClient.getUserById(userId))
                .thenReturn(userResponse(userId));

        when(bookClient.getBookById(bookId))
                .thenReturn(
                        bookResponse(
                                bookId,
                                "Microservicios",
                                false
                        )
                );

        when(
                reservationRepository
                        .existsByUserIdAndBookIdAndStatus(
                                userId,
                                bookId,
                                ReservationStatus.ACTIVE
                        )
        ).thenReturn(false);

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> {
                    Reservation reservation =
                            invocation.getArgument(0);

                    reservation.setId(reservationId);

                    return reservation;
                });

        when(
                notificationClient.createNotification(
                        any(NotificationClientRequest.class)
                )
        ).thenThrow(
                new ExternalServiceException(
                        "No fue posible crear la notificación"
                )
        );

        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> reservationService.createReservation(
                        request
                )
        );

        assertEquals(
                "No fue posible crear la notificación",
                exception.getMessage()
        );

        verify(reservationRepository).delete(
                any(Reservation.class)
        );
    }

    @Test
    void getReservationsByUserId_shouldReturnReservations() {
        ReservationServiceImpl reservationService =
                createService();

        UUID userId = UUID.randomUUID();

        Reservation firstReservation = reservationEntity(
                UUID.randomUUID(),
                userId,
                UUID.randomUUID(),
                ReservationStatus.ACTIVE
        );

        Reservation secondReservation = reservationEntity(
                UUID.randomUUID(),
                userId,
                UUID.randomUUID(),
                ReservationStatus.CANCELLED
        );

        when(reservationRepository.findByUserId(userId))
                .thenReturn(
                        List.of(
                                firstReservation,
                                secondReservation
                        )
                );

        ApiResponse<List<ReservationResponse>> result =
                reservationService.getReservationsByUserId(
                        userId
                );

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(2, result.data().size());
        assertEquals(
                userId,
                result.data().get(0).userId()
        );
        assertEquals(
                "Reservas obtenidas con éxito",
                result.message()
        );
    }

    @Test
    void getReservationsByUserId_shouldThrowWhenEmpty() {
        ReservationServiceImpl reservationService =
                createService();

        UUID userId = UUID.randomUUID();

        when(reservationRepository.findByUserId(userId))
                .thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () ->
                        reservationService
                                .getReservationsByUserId(
                                        userId
                                )
        );

        assertEquals(
                "No se encontraron reservas para el usuario: "
                        + userId,
                exception.getMessage()
        );
    }

    @Test
    void cancelReservation_shouldCancelActiveReservation() {
        ReservationServiceImpl reservationService =
                createService();

        UUID reservationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Reservation reservation = reservationEntity(
                reservationId,
                userId,
                UUID.randomUUID(),
                ReservationStatus.ACTIVE
        );

        when(reservationRepository.findById(reservationId))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        when(
                notificationClient.createNotification(
                        any(NotificationClientRequest.class)
                )
        ).thenReturn(
                notificationResponse(userId)
        );

        ApiResponse<ReservationResponse> result =
                reservationService.cancelReservation(
                        reservationId
                );

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(
                ReservationStatus.CANCELLED,
                result.data().status()
        );
        assertEquals(
                "Reserva cancelada con éxito",
                result.message()
        );

        verify(reservationRepository).save(reservation);

        verify(notificationClient).createNotification(
                any(NotificationClientRequest.class)
        );
    }

    @Test
    void cancelReservation_shouldRejectAlreadyCancelledReservation() {
        ReservationServiceImpl reservationService =
                createService();

        UUID reservationId = UUID.randomUUID();

        Reservation reservation = reservationEntity(
                reservationId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                ReservationStatus.CANCELLED
        );

        when(reservationRepository.findById(reservationId))
                .thenReturn(Optional.of(reservation));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> reservationService.cancelReservation(
                        reservationId
                )
        );

        assertEquals(
                "La reserva ya se encuentra cancelada",
                exception.getMessage()
        );

        verify(reservationRepository, never())
                .save(any(Reservation.class));

        verify(notificationClient, never())
                .createNotification(
                        any(NotificationClientRequest.class)
                );
    }

    @Test
    void cancelReservation_shouldThrowWhenReservationDoesNotExist() {
        ReservationServiceImpl reservationService =
                createService();

        UUID reservationId = UUID.randomUUID();

        when(reservationRepository.findById(reservationId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.cancelReservation(
                        reservationId
                )
        );

        assertEquals(
                "No se encontró la reserva con id: "
                        + reservationId,
                exception.getMessage()
        );
    }

    @Test
    void cancelReservation_shouldRestoreStatusWhenNotificationFails() {
        ReservationServiceImpl reservationService =
                createService();

        UUID reservationId = UUID.randomUUID();

        Reservation reservation = reservationEntity(
                reservationId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                ReservationStatus.ACTIVE
        );

        when(reservationRepository.findById(reservationId))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        when(
                notificationClient.createNotification(
                        any(NotificationClientRequest.class)
                )
        ).thenThrow(
                new ExternalServiceException(
                        "No fue posible crear la notificación"
                )
        );

        assertThrows(
                ExternalServiceException.class,
                () -> reservationService.cancelReservation(
                        reservationId
                )
        );

        assertEquals(
                ReservationStatus.ACTIVE,
                reservation.getStatus()
        );

        verify(
                reservationRepository,
                org.mockito.Mockito.times(2)
        ).save(reservation);
    }

    private UserClientResponse userResponse(UUID userId) {
        return new UserClientResponse(
                userId,
                "usuario@correo.cl",
                "Usuario Prueba",
                "999999999",
                "Santiago"
        );
    }

    private BookClientResponse bookResponse(
            UUID bookId,
            String title,
            boolean available
    ) {
        return new BookClientResponse(
                bookId,
                title,
                "Autor Prueba",
                "Tecnología",
                "ISBN-" + bookId,
                available
        );
    }

    private NotificationClientResponse notificationResponse(
            UUID userId
    ) {
        return new NotificationClientResponse(
                UUID.randomUUID(),
                userId,
                "Notificación creada",
                LocalDateTime.now()
        );
    }

    private Reservation reservationEntity(
            UUID reservationId,
            UUID userId,
            UUID bookId,
            ReservationStatus status
    ) {
        Reservation reservation = new Reservation();

        reservation.setId(reservationId);
        reservation.setUserId(userId);
        reservation.setBookId(bookId);
        reservation.setReservationDate(LocalDate.now());
        reservation.setStatus(status);

        return reservation;
    }
}
