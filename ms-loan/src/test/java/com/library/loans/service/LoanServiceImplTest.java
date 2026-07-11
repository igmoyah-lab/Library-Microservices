package com.library.loans.service;

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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.loans.client.BookClient;
import com.library.loans.client.UserClient;
import com.library.loans.client.dto.BookClientResponse;
import com.library.loans.client.dto.UserClientResponse;
import com.library.loans.dto.ApiResponse;
import com.library.loans.dto.LoanRequest;
import com.library.loans.dto.LoanResponse;
import com.library.loans.entity.Loan;
import com.library.loans.entity.LoanStatus;
import com.library.loans.exception.BusinessRuleException;
import com.library.loans.exception.ExternalServiceException;
import com.library.loans.exception.ResourceNotFoundException;
import com.library.loans.repository.LoanRepository;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private BookClient bookClient;

    @Test
    void createLoan_shouldCreateLoanAndMarkBookUnavailable() {
        LoanServiceImpl loanService = createService();

        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UUID loanId = UUID.randomUUID();

        LoanRequest request = new LoanRequest(
                userId,
                bookId
        );

        UserClientResponse user = userResponse(userId);

        BookClientResponse availableBook =
                bookResponse(bookId, true);

        BookClientResponse unavailableBook =
                bookResponse(bookId, false);

        when(userClient.getUserById(userId))
                .thenReturn(user);

        when(bookClient.getBookById(bookId))
                .thenReturn(availableBook);

        when(
                loanRepository.existsByBookIdAndStatus(
                        bookId,
                        LoanStatus.ACTIVE
                )
        ).thenReturn(false);

        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(invocation -> {
                    Loan loan = invocation.getArgument(0);
                    loan.setId(loanId);
                    return loan;
                });

        when(bookClient.updateAvailability(bookId, false))
                .thenReturn(unavailableBook);

        ApiResponse<LoanResponse> result =
                loanService.createLoan(request);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(loanId, result.data().id());
        assertEquals(userId, result.data().userId());
        assertEquals(bookId, result.data().bookId());
        assertEquals(
                LoanStatus.ACTIVE,
                result.data().status()
        );
        assertEquals(
                result.data().loanDate().plusDays(7),
                result.data().dueDate()
        );
        assertEquals(
                "Préstamo creado con éxito",
                result.message()
        );

        ArgumentCaptor<Loan> loanCaptor =
                ArgumentCaptor.forClass(Loan.class);

        verify(loanRepository).save(
                loanCaptor.capture()
        );

        Loan savedLoan = loanCaptor.getValue();

        assertEquals(userId, savedLoan.getUserId());
        assertEquals(bookId, savedLoan.getBookId());
        assertEquals(
                LoanStatus.ACTIVE,
                savedLoan.getStatus()
        );
        assertNotNull(savedLoan.getLoanDate());
        assertEquals(
                savedLoan.getLoanDate().plusDays(7),
                savedLoan.getDueDate()
        );

        verify(userClient).getUserById(userId);
        verify(bookClient).getBookById(bookId);
        verify(bookClient).updateAvailability(
                bookId,
                false
        );
    }

    @Test
    void createLoan_shouldRejectBookWithActiveLoan() {
        LoanServiceImpl loanService = createService();

        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        LoanRequest request = new LoanRequest(
                userId,
                bookId
        );

        when(userClient.getUserById(userId))
                .thenReturn(userResponse(userId));

        when(bookClient.getBookById(bookId))
                .thenReturn(bookResponse(bookId, true));

        when(
                loanRepository.existsByBookIdAndStatus(
                        bookId,
                        LoanStatus.ACTIVE
                )
        ).thenReturn(true);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> loanService.createLoan(request)
        );

        assertEquals(
                "El libro ya posee un préstamo activo",
                exception.getMessage()
        );

        verify(loanRepository, never())
                .save(any(Loan.class));

        verify(bookClient, never())
                .updateAvailability(bookId, false);
    }

    @Test
    void createLoan_shouldRejectUnavailableBook() {
        LoanServiceImpl loanService = createService();

        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        LoanRequest request = new LoanRequest(
                userId,
                bookId
        );

        when(userClient.getUserById(userId))
                .thenReturn(userResponse(userId));

        when(bookClient.getBookById(bookId))
                .thenReturn(bookResponse(bookId, false));

        when(
                loanRepository.existsByBookIdAndStatus(
                        bookId,
                        LoanStatus.ACTIVE
                )
        ).thenReturn(false);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> loanService.createLoan(request)
        );

        assertEquals(
                "El libro no se encuentra disponible",
                exception.getMessage()
        );

        verify(loanRepository, never())
                .save(any(Loan.class));

        verify(bookClient, never())
                .updateAvailability(bookId, false);
    }

    @Test
    void createLoan_shouldDeleteLoanWhenBookUpdateFails() {
        LoanServiceImpl loanService = createService();

        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UUID loanId = UUID.randomUUID();

        LoanRequest request = new LoanRequest(
                userId,
                bookId
        );

        when(userClient.getUserById(userId))
                .thenReturn(userResponse(userId));

        when(bookClient.getBookById(bookId))
                .thenReturn(bookResponse(bookId, true));

        when(
                loanRepository.existsByBookIdAndStatus(
                        bookId,
                        LoanStatus.ACTIVE
                )
        ).thenReturn(false);

        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(invocation -> {
                    Loan loan = invocation.getArgument(0);
                    loan.setId(loanId);
                    return loan;
                });

        ExternalServiceException externalException =
                new ExternalServiceException(
                        "No fue posible actualizar el libro en ms-book"
                );

        when(bookClient.updateAvailability(bookId, false))
                .thenThrow(externalException);

        ExternalServiceException result = assertThrows(
                ExternalServiceException.class,
                () -> loanService.createLoan(request)
        );

        assertEquals(
                "No fue posible actualizar el libro en ms-book",
                result.getMessage()
        );

        ArgumentCaptor<Loan> loanCaptor =
                ArgumentCaptor.forClass(Loan.class);

        verify(loanRepository).delete(
                loanCaptor.capture()
        );

        assertEquals(
                loanId,
                loanCaptor.getValue().getId()
        );
    }

    @Test
    void getLoansByUserId_shouldReturnLoans() {
        LoanServiceImpl loanService = createService();

        UUID userId = UUID.randomUUID();

        Loan firstLoan = loanEntity(
                UUID.randomUUID(),
                userId,
                UUID.randomUUID(),
                LoanStatus.ACTIVE
        );

        Loan secondLoan = loanEntity(
                UUID.randomUUID(),
                userId,
                UUID.randomUUID(),
                LoanStatus.RETURNED
        );

        when(loanRepository.findByUserId(userId))
                .thenReturn(
                        List.of(firstLoan, secondLoan)
                );

        ApiResponse<List<LoanResponse>> result =
                loanService.getLoansByUserId(userId);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(2, result.data().size());
        assertEquals(
                userId,
                result.data().get(0).userId()
        );
        assertEquals(
                "Préstamos obtenidos con éxito",
                result.message()
        );
    }

    @Test
    void getLoansByUserId_shouldThrowWhenNoLoansExist() {
        LoanServiceImpl loanService = createService();

        UUID userId = UUID.randomUUID();

        when(loanRepository.findByUserId(userId))
                .thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> loanService.getLoansByUserId(userId)
        );

        assertEquals(
                "No se encontraron préstamos para "
                        + "el usuario con id: "
                        + userId,
                exception.getMessage()
        );
    }

    @Test
    void countLoans_shouldReturnRepositoryCount() {
        LoanServiceImpl loanService = createService();

        when(loanRepository.count())
                .thenReturn(12L);

        long result = loanService.countLoans();

        assertEquals(12L, result);
        verify(loanRepository).count();
    }

    private LoanServiceImpl createService() {
        return new LoanServiceImpl(
                loanRepository,
                userClient,
                bookClient
        );
    }

    private UserClientResponse userResponse(UUID userId) {
        return new UserClientResponse(
                userId,
                "usuario@correo.cl",
                "Usuario Prueba",
                "912345678",
                "Santiago"
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

    private Loan loanEntity(
            UUID loanId,
            UUID userId,
            UUID bookId,
            LoanStatus status
    ) {
        Loan loan = new Loan();

        LocalDate loanDate = LocalDate.now();

        loan.setId(loanId);
        loan.setUserId(userId);
        loan.setBookId(bookId);
        loan.setLoanDate(loanDate);
        loan.setDueDate(loanDate.plusDays(7));
        loan.setStatus(status);

        return loan;
    }
}
