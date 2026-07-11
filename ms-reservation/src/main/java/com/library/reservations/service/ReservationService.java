package com.library.reservations.service;

import java.util.List;
import java.util.UUID;

import com.library.reservations.dto.ApiResponse;
import com.library.reservations.dto.ReservationRequest;
import com.library.reservations.dto.ReservationResponse;

public interface ReservationService {

    /**
     * Crea una reserva después de validar al usuario,
     * el libro y las reglas del negocio.
     *
     * @param reservationRequest datos necesarios para reservar
     * @return respuesta con la reserva creada
     */
    ApiResponse<ReservationResponse> createReservation(
            ReservationRequest reservationRequest
    );

    /**
     * Obtiene las reservas pertenecientes a un usuario.
     *
     * @param userId identificador del usuario
     * @return respuesta con las reservas encontradas
     */
    ApiResponse<List<ReservationResponse>> getReservationsByUserId(
            UUID userId
    );

    /**
     * Cancela una reserva activa.
     *
     * @param id identificador de la reserva
     * @return respuesta con la reserva cancelada
     */
    ApiResponse<ReservationResponse> cancelReservation(UUID id);
}