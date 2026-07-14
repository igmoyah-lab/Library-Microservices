package com.library.bff.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.ReservationRequest;
import com.library.bff.dto.ReservationResponse;
import com.library.bff.services.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }

    /**
     * Crea una reserva mediante el BFF.
     *
     * @param request datos de la reserva
     * @return reserva creada
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request
    ) {
        return reservationService.createReservation(request);
    }

    /**
     * Obtiene las reservas de un usuario.
     *
     * @param userId identificador del usuario
     * @return reservas encontradas
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<ReservationResponse>>
            getReservationsByUserId(
                    @PathVariable UUID userId
            ) {
        return reservationService
                .getReservationsByUserId(userId);
    }

    /**
     * Cancela una reserva mediante su ID.
     *
     * @param reservationId identificador de la reserva
     * @return reserva cancelada
     */
    @DeleteMapping("/{reservationId}")
    public ApiResponse<ReservationResponse> cancelReservation(
            @PathVariable UUID reservationId
    ) {
        return reservationService
                .cancelReservation(reservationId);
    }
}
