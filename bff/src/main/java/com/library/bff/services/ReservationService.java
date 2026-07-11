package com.library.bff.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.bff.client.ReservationClient;
import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.ReservationRequest;
import com.library.bff.dto.ReservationResponse;

@Service
public class ReservationService {

    private final ReservationClient reservationClient;

    public ReservationService(
            ReservationClient reservationClient
    ) {
        this.reservationClient = reservationClient;
    }

    /**
     * Crea una reserva mediante ms-reservation.
     *
     * @param request datos de la reserva
     * @return reserva creada
     */
    public ApiResponse<ReservationResponse> createReservation(
            ReservationRequest request
    ) {
        return reservationClient.createReservation(request);
    }

    /**
     * Obtiene las reservas de un usuario.
     *
     * @param userId identificador del usuario
     * @return reservas encontradas
     */
    public ApiResponse<List<ReservationResponse>>
            getReservationsByUserId(
                    UUID userId
            ) {
        return reservationClient
                .getReservationsByUserId(userId);
    }

    /**
     * Cancela una reserva mediante su ID.
     *
     * @param reservationId identificador de la reserva
     * @return reserva cancelada
     */
    public ApiResponse<ReservationResponse> cancelReservation(
            UUID reservationId
    ) {
        return reservationClient
                .cancelReservation(reservationId);
    }
}
