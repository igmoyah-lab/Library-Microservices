package com.library.reservations.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.reservations.dto.ApiResponse;
import com.library.reservations.dto.ReservationRequest;
import com.library.reservations.dto.ReservationResponse;
import com.library.reservations.service.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>>
            createReservation(
                    @Valid @RequestBody
                    ReservationRequest reservationRequest
            ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        reservationService.createReservation(
                                reservationRequest
                        )
                );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>>
            getReservationsByUserId(
                    @PathVariable UUID userId
            ) {

        return ResponseEntity.ok(
                reservationService.getReservationsByUserId(userId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationResponse>>
            cancelReservation(
                    @PathVariable UUID id
            ) {

        return ResponseEntity.ok(
                reservationService.cancelReservation(id)
        );
    }
}