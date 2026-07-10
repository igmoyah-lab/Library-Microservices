package com.library.reservations.service;

import java.util.List;
import java.util.UUID;

import com.library.reservations.dto.ApiResponse;
import com.library.reservations.dto.ReservationRequest;
import com.library.reservations.dto.ReservationResponse;

public interface ReservationService {

    ApiResponse<ReservationResponse> createReservation(
            ReservationRequest reservationRequest
    );

    ApiResponse<List<ReservationResponse>> getReservationsByUserId(
            UUID userId
    );

    ApiResponse<ReservationResponse> cancelReservation(UUID id);
}