package com.library.reservations.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.reservations.client.BookClient;
import com.library.reservations.client.NotificationClient;
import com.library.reservations.client.UserClient;
import com.library.reservations.client.dto.BookClientResponse;
import com.library.reservations.client.dto.NotificationClientRequest;
import com.library.reservations.dto.ApiResponse;
import com.library.reservations.dto.ReservationRequest;
import com.library.reservations.dto.ReservationResponse;
import com.library.reservations.entity.Reservation;
import com.library.reservations.entity.ReservationStatus;
import com.library.reservations.exception.BusinessRuleException;
import com.library.reservations.exception.DuplicateResourceException;
import com.library.reservations.exception.ResourceNotFoundException;
import com.library.reservations.repository.ReservationRepository;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserClient userClient;
    private final BookClient bookClient;
    private final NotificationClient notificationClient;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            UserClient userClient,
            BookClient bookClient,
            NotificationClient notificationClient
    ) {
        this.reservationRepository = reservationRepository;
        this.userClient = userClient;
        this.bookClient = bookClient;
        this.notificationClient = notificationClient;
    }

    /**
     * Crea una reserva después de validar que el usuario y
     * el libro existan, que el libro no esté disponible y
     * que no exista otra reserva activa igual.
     *
     * @param reservationRequest datos necesarios para reservar
     * @return respuesta con la reserva creada
     * @throws BusinessRuleException si el libro está disponible
     * @throws DuplicateResourceException si ya existe una reserva activa
     */
    @Override
    public ApiResponse<ReservationResponse> createReservation(
            ReservationRequest reservationRequest
    ) {
        UUID userId = reservationRequest.userId();
        UUID bookId = reservationRequest.bookId();

        /*
         * Valida que el usuario exista en ms-user.
         */
        userClient.getUserById(userId);

        /*
         * Consulta el libro en ms-book.
         */
        BookClientResponse book =
                bookClient.getBookById(bookId);

        /*
         * Un libro disponible puede prestarse directamente,
         * por lo que no corresponde reservarlo.
         */
        if (Boolean.TRUE.equals(book.available())) {
            throw new BusinessRuleException(
                    "El libro se encuentra disponible y no necesita reserva"
            );
        }

        boolean reservationExists =
                reservationRepository
                        .existsByUserIdAndBookIdAndStatus(
                                userId,
                                bookId,
                                ReservationStatus.ACTIVE
                        );

        if (reservationExists) {
            throw new DuplicateResourceException(
                    "El usuario ya tiene una reserva activa para este libro"
            );
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setBookId(bookId);
        reservation.setReservationDate(LocalDate.now());
        reservation.setStatus(ReservationStatus.ACTIVE);

        Reservation savedReservation =
                reservationRepository.save(reservation);

        try {
            notificationClient.createNotification(
                    new NotificationClientRequest(
                            userId,
                            "Tu reserva para el libro '"
                                    + book.title()
                                    + "' fue creada correctamente"
                    )
            );

        } catch (RuntimeException exception) {
            /*
             * Si la notificación falla, se elimina la reserva
             * para evitar devolver un error con datos guardados.
             */
            reservationRepository.delete(savedReservation);

            throw exception;
        }

        return new ApiResponse<>(
                true,
                mapToResponse(savedReservation),
                "Reserva creada con éxito"
        );
    }

    /**
     * Obtiene todas las reservas pertenecientes
     * a un usuario.
     *
     * @param userId identificador del usuario
     * @return respuesta con las reservas encontradas
     * @throws ResourceNotFoundException si no existen reservas
     */
    @Override
    public ApiResponse<List<ReservationResponse>>
            getReservationsByUserId(
                    UUID userId
            ) {
        List<Reservation> reservations =
                reservationRepository.findByUserId(userId);

        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron reservas para el usuario: "
                            + userId
            );
        }

        List<ReservationResponse> responses =
                reservations.stream()
                        .map(this::mapToResponse)
                        .toList();

        return new ApiResponse<>(
                true,
                responses,
                "Reservas obtenidas con éxito"
        );
    }

    /**
     * Cancela una reserva activa y genera una notificación
     * para el usuario.
     *
     * @param id identificador de la reserva
     * @return respuesta con la reserva cancelada
     * @throws ResourceNotFoundException si la reserva no existe
     * @throws BusinessRuleException si ya estaba cancelada
     */
    @Override
    public ApiResponse<ReservationResponse> cancelReservation(
            UUID id
    ) {
        Reservation reservation = findReservationById(id);

        if (reservation.getStatus()
                == ReservationStatus.CANCELLED) {
            throw new BusinessRuleException(
                    "La reserva ya se encuentra cancelada"
            );
        }

        reservation.setStatus(
                ReservationStatus.CANCELLED
        );

        Reservation updatedReservation =
                reservationRepository.save(reservation);

        try {
            notificationClient.createNotification(
                    new NotificationClientRequest(
                            reservation.getUserId(),
                            "Tu reserva fue cancelada correctamente"
                    )
            );

        } catch (RuntimeException exception) {
            /*
             * Si la notificación falla, se restaura
             * el estado anterior de la reserva.
             */
            reservation.setStatus(
                    ReservationStatus.ACTIVE
            );

            reservationRepository.save(reservation);

            throw exception;
        }

        return new ApiResponse<>(
                true,
                mapToResponse(updatedReservation),
                "Reserva cancelada con éxito"
        );
    }

    /**
     * Busca internamente una reserva mediante su ID.
     *
     * @param id identificador de la reserva
     * @return reserva encontrada
     * @throws ResourceNotFoundException si la reserva no existe
     */
    private Reservation findReservationById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró la reserva con id: "
                                        + id
                        )
                );
    }

    /**
     * Convierte una entidad Reservation
     * en ReservationResponse.
     *
     * @param reservation entidad de reserva
     * @return DTO de respuesta
     */
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