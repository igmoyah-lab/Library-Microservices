package com.library.reservations.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.library.reservations.entity.Reservation;
import com.library.reservations.entity.ReservationStatus;

@DataJpaTest
@ActiveProfiles("test")
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void findByUserId_shouldReturnReservationsFromSpecifiedUser() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        Reservation firstReservation = createReservation(
                userId,
                UUID.randomUUID(),
                ReservationStatus.ACTIVE
        );

        Reservation secondReservation = createReservation(
                userId,
                UUID.randomUUID(),
                ReservationStatus.CANCELLED
        );

        Reservation otherUserReservation = createReservation(
                otherUserId,
                UUID.randomUUID(),
                ReservationStatus.ACTIVE
        );

        reservationRepository.saveAllAndFlush(
                List.of(
                        firstReservation,
                        secondReservation,
                        otherUserReservation
                )
        );

        List<Reservation> result =
                reservationRepository.findByUserId(userId);

        assertEquals(2, result.size());

        assertTrue(
                result.stream()
                        .allMatch(reservation ->
                                reservation.getUserId().equals(userId)
                        )
        );
    }

    @Test
    void findByUserId_shouldReturnEmptyList_whenUserHasNoReservations() {
        UUID userId = UUID.randomUUID();

        List<Reservation> result =
                reservationRepository.findByUserId(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByUserIdAndBookIdAndStatus_shouldReturnTrue_whenReservationExists() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        Reservation reservation = createReservation(
                userId,
                bookId,
                ReservationStatus.ACTIVE
        );

        reservationRepository.saveAndFlush(reservation);

        boolean result =
                reservationRepository
                        .existsByUserIdAndBookIdAndStatus(
                                userId,
                                bookId,
                                ReservationStatus.ACTIVE
                        );

        assertTrue(result);
    }

    @Test
    void existsByUserIdAndBookIdAndStatus_shouldReturnFalse_whenReservationDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        boolean result =
                reservationRepository
                        .existsByUserIdAndBookIdAndStatus(
                                userId,
                                bookId,
                                ReservationStatus.ACTIVE
                        );

        assertFalse(result);
    }

    @Test
    void existsByUserIdAndBookIdAndStatus_shouldReturnFalse_whenStatusIsDifferent() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        Reservation reservation = createReservation(
                userId,
                bookId,
                ReservationStatus.CANCELLED
        );

        reservationRepository.saveAndFlush(reservation);

        boolean result =
                reservationRepository
                        .existsByUserIdAndBookIdAndStatus(
                                userId,
                                bookId,
                                ReservationStatus.ACTIVE
                        );

        assertFalse(result);
    }

    @Test
    void existsByUserIdAndBookIdAndStatus_shouldNotMatchAnotherUser() {
        UUID searchedUserId = UUID.randomUUID();
        UUID savedUserId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        Reservation reservation = createReservation(
                savedUserId,
                bookId,
                ReservationStatus.ACTIVE
        );

        reservationRepository.saveAndFlush(reservation);

        boolean result =
                reservationRepository
                        .existsByUserIdAndBookIdAndStatus(
                                searchedUserId,
                                bookId,
                                ReservationStatus.ACTIVE
                        );

        assertFalse(result);
    }

    @Test
    void existsByUserIdAndBookIdAndStatus_shouldNotMatchAnotherBook() {
        UUID userId = UUID.randomUUID();
        UUID searchedBookId = UUID.randomUUID();
        UUID savedBookId = UUID.randomUUID();

        Reservation reservation = createReservation(
                userId,
                savedBookId,
                ReservationStatus.ACTIVE
        );

        reservationRepository.saveAndFlush(reservation);

        boolean result =
                reservationRepository
                        .existsByUserIdAndBookIdAndStatus(
                                userId,
                                searchedBookId,
                                ReservationStatus.ACTIVE
                        );

        assertFalse(result);
    }

    private Reservation createReservation(
            UUID userId,
            UUID bookId,
            ReservationStatus status
    ) {
        Reservation reservation = new Reservation();

        reservation.setUserId(userId);
        reservation.setBookId(bookId);
        reservation.setReservationDate(LocalDate.now());
        reservation.setStatus(status);
        

        return reservation;
    }
}