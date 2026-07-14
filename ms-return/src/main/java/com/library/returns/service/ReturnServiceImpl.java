package com.library.returns.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.returns.client.BookClient;
import com.library.returns.client.FineClient;
import com.library.returns.client.LoanClient;
import com.library.returns.client.dto.FineClientRequest;
import com.library.returns.client.dto.LoanClientResponse;
import com.library.returns.dto.ApiResponse;
import com.library.returns.dto.ReturnRequest;
import com.library.returns.dto.ReturnResponse;
import com.library.returns.entity.BookReturn;
import com.library.returns.exception.BusinessRuleException;
import com.library.returns.exception.DuplicateResourceException;
import com.library.returns.exception.ExternalServiceException;
import com.library.returns.exception.ResourceNotFoundException;
import com.library.returns.repository.ReturnRepository;

@Service
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRepository returnRepository;
    private final LoanClient loanClient;
    private final BookClient bookClient;
    private final FineClient fineClient;

    public ReturnServiceImpl(
            ReturnRepository returnRepository,
            LoanClient loanClient,
            BookClient bookClient,
            FineClient fineClient
    ) {
        this.returnRepository = returnRepository;
        this.loanClient = loanClient;
        this.bookClient = bookClient;
        this.fineClient = fineClient;
    }

    /**
     * Registra una devolución después de consultar el préstamo
     * real en ms-loan. Calcula internamente los días de atraso,
     * libera el libro y genera una multa cuando corresponde.
     *
     * @param returnRequest datos de la devolución
     * @return devolución registrada
     * @throws DuplicateResourceException si ya existe una devolución
     * @throws BusinessRuleException si el préstamo no está activo
     * @throws ExternalServiceException si falla otro microservicio
     */
    @Override
    public ApiResponse<ReturnResponse> createReturn(
            ReturnRequest returnRequest
    ) {
        UUID loanId = returnRequest.loanId();

        if (returnRepository.existsByLoanId(loanId)) {
            throw new DuplicateResourceException(
                    "El préstamo ya tiene una devolución registrada"
            );
        }

        /*
         * La fecha de vencimiento y demás datos se obtienen
         * directamente desde ms-loan.
         */
        LoanClientResponse loan =
                loanClient.getLoanById(loanId);

        if (!"ACTIVE".equalsIgnoreCase(loan.status())) {
            throw new BusinessRuleException(
                    "Solo se pueden devolver préstamos activos"
            );
        }

        LocalDate returnDate = LocalDate.now();

        long calculatedDays = ChronoUnit.DAYS.between(
                loan.dueDate(),
                returnDate
        );

        int delayedDays = calculatedDays > 0
                ? Math.toIntExact(calculatedDays)
                : 0;

        boolean delayed = delayedDays > 0;

        BookReturn bookReturn = new BookReturn();
        bookReturn.setLoanId(loanId);
        bookReturn.setReturnDate(returnDate);
        bookReturn.setDelayed(delayed);

        BookReturn savedReturn =
                returnRepository.save(bookReturn);

        boolean bookReleased = false;

        try {
            /*
             * Primero se libera el libro para que vuelva
             * a estar disponible.
             */
            bookClient.updateAvailability(
                    loan.bookId(),
                    true
            );

            bookReleased = true;

            /*
             * Después se actualiza el estado del préstamo.
             */
            loanClient.markLoanAsReturned(loanId);

        } catch (RuntimeException exception) {
            /*
             * Si falla la actualización del préstamo,
             * intentamos volver a dejar el libro ocupado.
             */
            if (bookReleased) {
                try {
                    bookClient.updateAvailability(
                            loan.bookId(),
                            false
                    );
                } catch (RuntimeException ignored) {
                    /*
                     * La excepción original es la que se propaga.
                     */
                }
            }

            /*
             * También se elimina la devolución local
             * para evitar dejarla registrada a medias.
             */
            returnRepository.delete(savedReturn);

            throw exception;
        }

        /*
         * La multa se crea después de completar la devolución.
         * Los días de atraso nunca vienen desde el cliente.
         */
        if (delayed) {
            try {
                fineClient.createFine(
                        new FineClientRequest(
                                loan.userId(),
                                loanId,
                                delayedDays
                        )
                );

            } catch (RuntimeException exception) {
                /*
                 * La devolución ya ocurrió correctamente,
                 * pero se informa que falló la multa.
                 */
                throw new ExternalServiceException(
                        "La devolución fue registrada, pero no fue "
                                + "posible generar la multa",
                        exception
                );
            }
        }

        String message = delayed
                ? "Devolución registrada con atraso de "
                        + delayedDays
                        + " día(s)"
                : "Devolución registrada con éxito";

        return new ApiResponse<>(
                true,
                mapToResponse(savedReturn),
                message
        );
    }

    /**
     * Obtiene una devolución mediante el identificador
     * del préstamo.
     *
     * @param loanId identificador del préstamo
     * @return devolución encontrada
     * @throws ResourceNotFoundException si la devolución no existe
     */
    @Override
    public ApiResponse<ReturnResponse> getReturnByLoanId(
            UUID loanId
    ) {
        BookReturn bookReturn =
                returnRepository.findByLoanId(loanId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No se encontró una devolución "
                                                + "para el préstamo: "
                                                + loanId
                                )
                        );

        return new ApiResponse<>(
                true,
                mapToResponse(bookReturn),
                "Devolución encontrada con éxito"
        );
    }

    /**
     * Convierte una entidad BookReturn en ReturnResponse.
     *
     * @param bookReturn entidad de devolución
     * @return datos de la devolución
     */
    private ReturnResponse mapToResponse(
            BookReturn bookReturn
    ) {
        return new ReturnResponse(
                bookReturn.getId(),
                bookReturn.getLoanId(),
                bookReturn.getReturnDate(),
                bookReturn.isDelayed()
        );
    }
}