package com.library.bff.client;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.ReservationRequest;
import com.library.bff.dto.ReservationResponse;

@Component
public class ReservationClient {

    private final RestClient restClient;

    public ReservationClient(
            @Value("${reservation.service.base-url}")
            String reservationBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(reservationBaseUrl)
                .build();
    }

    /**
     * Solicita la creación de una reserva.
     *
     * @param request datos de la reserva
     * @return reserva creada
     */
    public ApiResponse<ReservationResponse> createReservation(
            ReservationRequest request
    ) {
        return restClient.post()
                .uri("/api/reservations")
                .body(request)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<ReservationResponse>
                        >() {
                        }
                );
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
        return restClient.get()
                .uri(
                        "/api/reservations/user/{userId}",
                        userId
                )
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<List<ReservationResponse>>
                        >() {
                        }
                );
    }

    /**
     * Cancela una reserva.
     *
     * @param reservationId identificador de la reserva
     * @return reserva cancelada
     */
    public ApiResponse<ReservationResponse> cancelReservation(
            UUID reservationId
    ) {
        return restClient.delete()
                .uri(
                        "/api/reservations/{id}",
                        reservationId
                )
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<ReservationResponse>
                        >() {
                        }
                );
    }
}
