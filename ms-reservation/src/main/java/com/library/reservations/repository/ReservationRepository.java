package com.library.reservations.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.reservations.entity.Reservation;
import com.library.reservations.entity.ReservationStatus;

public interface ReservationRepository
        extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByUserId(UUID userId);

    boolean existsByUserIdAndBookIdAndStatus(
            UUID userId,
            UUID bookId,
            ReservationStatus status
    );
}