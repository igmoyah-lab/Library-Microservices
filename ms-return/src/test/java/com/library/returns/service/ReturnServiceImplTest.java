package com.library.returns.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.returns.client.BookClient;
import com.library.returns.client.FineClient;
import com.library.returns.client.LoanClient;
import com.library.returns.client.dto.BookClientResponse;
import com.library.returns.client.dto.FineClientRequest;
import com.library.returns.client.dto.FineClientResponse;
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

@ExtendWith(MockitoExtension.class)
class ReturnServiceImplTest {

    @Mock
    private ReturnRepository returnRepository;

    @Mock
    private LoanClient loanClient;

    @Mock
    private BookClient bookClient;

    @Mock
    private FineClient fineClient;

    @Test
    void createReturn_shouldRegisterReturnWithoutDelay() {
        ReturnServiceImpl returnService = createService();

        UUID returnId = UUID.randomUUID();
        UUID loanId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        ReturnRequest request = new ReturnRequest(loanId);

        LoanClientResponse activeLoan = loanResponse(
                loanId,
                userId,
                bookId,
                LocalDate.now().plusDays(2),
                "ACTIVE"
        );

        when(returnRepository.existsByLoanId(loanId))
                .thenReturn(false);

        when(loanClient.getLoanById(loanId))
                .thenReturn(activeLoan);

        when(returnRepository.save(any(BookReturn.class)))
                .thenAnswer(invocation -> {
                    BookReturn bookReturn = invocation.getArgument(0);
                    bookReturn.setId(returnId);
                    return bookReturn;
                });

        when(bookClient.updateAvailability(bookId, true))
                .thenReturn(bookResponse(bookId, true));

        when(loanClient.markLoanAsReturned(loanId))
                .thenReturn(
                        loanResponse(
                                loanId,
                                userId,
                                bookId,
                                activeLoan.dueDate(),
                                "RETURNED"
                        )
                );

        ApiResponse<ReturnResponse> result =
                returnService.createReturn(request);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(returnId, result.data().id());
        assertEquals(loanId, result.data().loanId());
        assertFalse(result.data().delayed());
        assertEquals(
                "Devolución registrada con éxito",
                result.message()
        );

        verify(bookClient).updateAvailability(bookId, true);
        verify(loanClient).markLoanAsReturned(loanId);
        verify(fineClient, never())
                .createFine(any(FineClientRequest.class));
    }

    @Test
    void createReturn_shouldGenerateFineWhenDelayed() {
        ReturnServiceImpl returnService = createService();

        UUID returnId = UUID.randomUUID();
        UUID loanId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        ReturnRequest request = new ReturnRequest(loanId);

        LoanClientResponse delayedLoan = loanResponse(
                loanId,
                userId,
                bookId,
                LocalDate.now().minusDays(3),
                "ACTIVE"
        );

        when(returnRepository.existsByLoanId(loanId))
                .thenReturn(false);

        when(loanClient.getLoanById(loanId))
                .thenReturn(delayedLoan);

        when(returnRepository.save(any(BookReturn.class)))
                .thenAnswer(invocation -> {
                    BookReturn bookReturn = invocation.getArgument(0);
                    bookReturn.setId(returnId);
                    return bookReturn;
                });

        when(bookClient.updateAvailability(bookId, true))
                .thenReturn(bookResponse(bookId, true));

        when(loanClient.markLoanAsReturned(loanId))
                .thenReturn(
                        loanResponse(
                                loanId,
                                userId,
                                bookId,
                                delayedLoan.dueDate(),
                                "RETURNED"
                        )
                );

        when(fineClient.createFine(any(FineClientRequest.class)))
                .thenReturn(
                        fineResponse(
                                userId,
                                loanId
                        )
                );

        ApiResponse<ReturnResponse> result =
                returnService.createReturn(request);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertTrue(result.data().delayed());
        assertEquals(
                "Devolución registrada con atraso de 3 día(s)",
                result.message()
        );

        ArgumentCaptor<FineClientRequest> fineCaptor =
                ArgumentCaptor.forClass(FineClientRequest.class);

        verify(fineClient).createFine(fineCaptor.capture());

        FineClientRequest fineRequest = fineCaptor.getValue();

        assertEquals(userId, fineRequest.userId());
        assertEquals(loanId, fineRequest.loanId());
        assertEquals(3, fineRequest.delayedDays());
    }

    @Test
    void createReturn_shouldRejectDuplicateReturn() {
        ReturnServiceImpl returnService = createService();

        UUID loanId = UUID.randomUUID();

        when(returnRepository.existsByLoanId(loanId))
                .thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> returnService.createReturn(
                        new ReturnRequest(loanId)
                )
        );

        assertEquals(
                "El préstamo ya tiene una devolución registrada",
                exception.getMessage()
        );

        verify(loanClient, never())
                .getLoanById(loanId);

        verify(returnRepository, never())
                .save(any(BookReturn.class));
    }

    @Test
    void createReturn_shouldRejectInactiveLoan() {
        ReturnServiceImpl returnService = createService();

        UUID loanId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        when(returnRepository.existsByLoanId(loanId))
                .thenReturn(false);

        when(loanClient.getLoanById(loanId))
                .thenReturn(
                        loanResponse(
                                loanId,
                                userId,
                                bookId,
                                LocalDate.now(),
                                "RETURNED"
                        )
                );

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> returnService.createReturn(
                        new ReturnRequest(loanId)
                )
        );

        assertEquals(
                "Solo se pueden devolver préstamos activos",
                exception.getMessage()
        );

        verify(returnRepository, never())
                .save(any(BookReturn.class));

        verify(bookClient, never())
                .updateAvailability(bookId, true);
    }

    @Test
    void createReturn_shouldRollbackWhenLoanUpdateFails() {
        ReturnServiceImpl returnService = createService();

        UUID returnId = UUID.randomUUID();
        UUID loanId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        LoanClientResponse activeLoan = loanResponse(
                loanId,
                userId,
                bookId,
                LocalDate.now().plusDays(1),
                "ACTIVE"
        );

        when(returnRepository.existsByLoanId(loanId))
                .thenReturn(false);

        when(loanClient.getLoanById(loanId))
                .thenReturn(activeLoan);

        when(returnRepository.save(any(BookReturn.class)))
                .thenAnswer(invocation -> {
                    BookReturn bookReturn = invocation.getArgument(0);
                    bookReturn.setId(returnId);
                    return bookReturn;
                });

        when(bookClient.updateAvailability(bookId, true))
                .thenReturn(bookResponse(bookId, true));

        when(loanClient.markLoanAsReturned(loanId))
                .thenThrow(
                        new ExternalServiceException(
                                "No fue posible actualizar el préstamo"
                        )
                );

        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> returnService.createReturn(
                        new ReturnRequest(loanId)
                )
        );

        assertEquals(
                "No fue posible actualizar el préstamo",
                exception.getMessage()
        );

        ArgumentCaptor<BookReturn> returnCaptor =
                ArgumentCaptor.forClass(BookReturn.class);

        verify(returnRepository).delete(returnCaptor.capture());

        assertEquals(
                returnId,
                returnCaptor.getValue().getId()
        );

        verify(bookClient).updateAvailability(bookId, true);
        verify(bookClient).updateAvailability(bookId, false);
    }

    @Test
    void createReturn_shouldReportFineFailureAfterReturn() {
        ReturnServiceImpl returnService = createService();

        UUID loanId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        LoanClientResponse delayedLoan = loanResponse(
                loanId,
                userId,
                bookId,
                LocalDate.now().minusDays(2),
                "ACTIVE"
        );

        when(returnRepository.existsByLoanId(loanId))
                .thenReturn(false);

        when(loanClient.getLoanById(loanId))
                .thenReturn(delayedLoan);

        when(returnRepository.save(any(BookReturn.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        when(bookClient.updateAvailability(bookId, true))
                .thenReturn(bookResponse(bookId, true));

        when(loanClient.markLoanAsReturned(loanId))
                .thenReturn(
                        loanResponse(
                                loanId,
                                userId,
                                bookId,
                                delayedLoan.dueDate(),
                                "RETURNED"
                        )
                );

        when(fineClient.createFine(any(FineClientRequest.class)))
                .thenThrow(
                        new ExternalServiceException(
                                "ms-fine no está disponible"
                        )
                );

        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> returnService.createReturn(
                        new ReturnRequest(loanId)
                )
        );

        assertEquals(
                "La devolución fue registrada, pero no fue "
                        + "posible generar la multa",
                exception.getMessage()
        );
    }

    @Test
    void getReturnByLoanId_shouldReturnRegisteredReturn() {
        ReturnServiceImpl returnService = createService();

        UUID returnId = UUID.randomUUID();
        UUID loanId = UUID.randomUUID();

        BookReturn bookReturn = returnEntity(
                returnId,
                loanId,
                false
        );

        when(returnRepository.findByLoanId(loanId))
                .thenReturn(Optional.of(bookReturn));

        ApiResponse<ReturnResponse> result =
                returnService.getReturnByLoanId(loanId);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(returnId, result.data().id());
        assertEquals(loanId, result.data().loanId());
        assertFalse(result.data().delayed());
        assertEquals(
                "Devolución encontrada con éxito",
                result.message()
        );
    }

    @Test
    void getReturnByLoanId_shouldThrowWhenMissing() {
        ReturnServiceImpl returnService = createService();

        UUID loanId = UUID.randomUUID();

        when(returnRepository.findByLoanId(loanId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> returnService.getReturnByLoanId(loanId)
        );

        assertEquals(
                "No se encontró una devolución "
                        + "para el préstamo: "
                        + loanId,
                exception.getMessage()
        );
    }

    private ReturnServiceImpl createService() {
        return new ReturnServiceImpl(
                returnRepository,
                loanClient,
                bookClient,
                fineClient
        );
    }

    private LoanClientResponse loanResponse(
            UUID loanId,
            UUID userId,
            UUID bookId,
            LocalDate dueDate,
            String status
    ) {
        return new LoanClientResponse(
                loanId,
                userId,
                bookId,
                dueDate.minusDays(7),
                dueDate,
                status
        );
    }

    private BookClientResponse bookResponse(
            UUID bookId,
            boolean available
    ) {
        return new BookClientResponse(
                bookId,
                "El Principito",
                "Antoine de Saint-Exupéry",
                "Literatura",
                "123456789",
                available
        );
    }

    private FineClientResponse fineResponse(
            UUID userId,
            UUID loanId
    ) {
        return new FineClientResponse(
                UUID.randomUUID(),
                userId,
                loanId,
                java.math.BigDecimal.valueOf(3000),
                LocalDate.now(),
                "PENDING"
        );
    }

    private BookReturn returnEntity(
            UUID returnId,
            UUID loanId,
            boolean delayed
    ) {
        BookReturn bookReturn = new BookReturn();

        bookReturn.setId(returnId);
        bookReturn.setLoanId(loanId);
        bookReturn.setReturnDate(LocalDate.now());
        bookReturn.setDelayed(delayed);

        return bookReturn;
    }
}
