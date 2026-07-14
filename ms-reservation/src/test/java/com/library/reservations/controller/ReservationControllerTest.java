package com.library.reservations.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.library.reservations.dto.ApiResponse;
import com.library.reservations.dto.ReservationRequest;
import com.library.reservations.dto.ReservationResponse;
import com.library.reservations.service.ReservationService;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private ReservationRequest reservationRequest;

    @Mock
    private ReservationResponse reservationResponse;

    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        reservationController =
                new ReservationController(reservationService);
    }

    @Test
    void createReservation_shouldReturnCreatedStatus() {
        ApiResponse<ReservationResponse> serviceResponse =
                new ApiResponse<>(
                        true,
                        reservationResponse,
                        "Reserva creada con éxito"
                );

        when(
                reservationService.createReservation(
                        reservationRequest
                )
        ).thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<ReservationResponse>> response =
                reservationController.createReservation(
                        reservationRequest
                );

        assertNotNull(response);
        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );
        assertSame(serviceResponse, response.getBody());

        verify(reservationService)
                .createReservation(reservationRequest);
    }

    @Test
    void getReservationsByUserId_shouldReturnOkStatus() {
        UUID userId = UUID.randomUUID();

        List<ReservationResponse> reservations =
                List.of(reservationResponse);

        ApiResponse<List<ReservationResponse>> serviceResponse =
                new ApiResponse<>(
                        true,
                        reservations,
                        "Reservas obtenidas con éxito"
                );

        when(
                reservationService.getReservationsByUserId(userId)
        ).thenReturn(serviceResponse);

        ResponseEntity<
                ApiResponse<List<ReservationResponse>>
        > response =
                reservationController.getReservationsByUserId(
                        userId
                );

        assertNotNull(response);
        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );
        assertSame(serviceResponse, response.getBody());

        verify(reservationService)
                .getReservationsByUserId(userId);
    }

    @Test
    void cancelReservation_shouldReturnOkStatus() {
        UUID reservationId = UUID.randomUUID();

        ApiResponse<ReservationResponse> serviceResponse =
                new ApiResponse<>(
                        true,
                        reservationResponse,
                        "Reserva cancelada con éxito"
                );

        when(
                reservationService.cancelReservation(reservationId)
        ).thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<ReservationResponse>> response =
                reservationController.cancelReservation(
                        reservationId
                );

        assertNotNull(response);
        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );
        assertSame(serviceResponse, response.getBody());

        verify(reservationService)
                .cancelReservation(reservationId);
    }
}