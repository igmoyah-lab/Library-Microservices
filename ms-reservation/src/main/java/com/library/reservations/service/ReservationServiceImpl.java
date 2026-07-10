package com.library.reservations.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.reservations.dto.ApiResponse;
import com.library.reservations.dto.ReservationRequest;
import com.library.reservations.dto.ReservationResponse;
import com.library.reservations.entity.Reservation;
import com.library.reservations.entity.ReservationStatus;
import com.library.reservations.exception.DuplicateResourceException;
import com.library.reservations.exception.ResourceNotFoundException;
import com.library.reservations.repository.ReservationRepository;


@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository
    ) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public ApiResponse<ReservationResponse> createReservation(
            ReservationRequest reservationRequest
    ) {

        boolean reservationExists =
                reservationRepository.existsByUserIdAndBookIdAndStatus(
                        reservationRequest.userId(),
                        reservationRequest.bookId(),
                        ReservationStatus.ACTIVE
                );

        if (reservationExists) {
            throw new DuplicateResourceException(
                    "El usuario ya tiene una reserva activa para este libro"
            );
        }

        Reservation reservation = new Reservation();

        reservation.setUserId(reservationRequest.userId());
        reservation.setBookId(reservationRequest.bookId());
        reservation.setReservationDate(LocalDate.now());
        reservation.setStatus(ReservationStatus.ACTIVE);

        Reservation savedReservation =
                reservationRepository.save(reservation);

        return new ApiResponse<>(
                true,
                mapToResponse(savedReservation),
                "Reserva creada con éxito"
        );
    }

    @Override
    public ApiResponse<List<ReservationResponse>> getReservationsByUserId(
            UUID userId
    ) {

        List<Reservation> reservations =
                reservationRepository.findByUserId(userId);

        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron reservas para el usuario: " + userId
            );
        }

        List<ReservationResponse> responses = reservations.stream()
                .map(this::mapToResponse)
                .toList();

        return new ApiResponse<>(
                true,
                responses,
                "Reservas obtenidas con éxito"
        );
    }

    @Override
    public ApiResponse<ReservationResponse> cancelReservation(UUID id) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la reserva con id: " + id
                ));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new DuplicateResourceException(
                    "La reserva ya se encuentra cancelada"
            );
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        Reservation updatedReservation =
                reservationRepository.save(reservation);

        return new ApiResponse<>(
                true,
                mapToResponse(updatedReservation),
                "Reserva cancelada con éxito"
        );
    }

    private ReservationResponse mapToResponse(
            Reservation reservation
    ) {

        return new ReservationResponse(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getBookId(),
                reservation.getReservationDate(),
                reservation.getStatus()
        );
    }
}