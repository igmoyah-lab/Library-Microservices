package com.library.fines.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.fines.dto.ApiResponse;
import com.library.fines.dto.FineRequest;
import com.library.fines.dto.FineResponse;
import com.library.fines.entity.Fine;
import com.library.fines.entity.FineStatus;
import com.library.fines.exception.BusinessRuleException;
import com.library.fines.exception.DuplicateResourceException;
import com.library.fines.exception.ResourceNotFoundException;
import com.library.fines.repository.FineRepository;

@ExtendWith(MockitoExtension.class)
class FineServiceImplTest {

    @Mock
    private FineRepository fineRepository;

    @Test
    void createFine_shouldCalculateAmountAndSaveFine() {
        FineServiceImpl fineService =
                new FineServiceImpl(fineRepository);

        UUID fineId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID loanId = UUID.randomUUID();

        FineRequest request = new FineRequest(
                userId,
                loanId,
                3
        );

        when(fineRepository.existsByLoanId(loanId))
                .thenReturn(false);

        when(fineRepository.save(any(Fine.class)))
                .thenAnswer(invocation -> {
                    Fine fine = invocation.getArgument(0);
                    fine.setId(fineId);
                    return fine;
                });

        ApiResponse<FineResponse> result =
                fineService.createFine(request);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(fineId, result.data().id());
        assertEquals(userId, result.data().userId());
        assertEquals(loanId, result.data().loanId());
        assertEquals(
                BigDecimal.valueOf(3000),
                result.data().amount()
        );
        assertEquals(
                FineStatus.PENDING,
                result.data().status()
        );
        assertEquals(
                "Multa registrada con éxito",
                result.message()
        );

        ArgumentCaptor<Fine> fineCaptor =
                ArgumentCaptor.forClass(Fine.class);

        verify(fineRepository).save(fineCaptor.capture());

        Fine savedFine = fineCaptor.getValue();

        assertEquals(userId, savedFine.getUserId());
        assertEquals(loanId, savedFine.getLoanId());
        assertEquals(
                BigDecimal.valueOf(3000),
                savedFine.getAmount()
        );
        assertEquals(
                FineStatus.PENDING,
                savedFine.getStatus()
        );
        assertNotNull(savedFine.getFineDate());
    }

    @Test
    void createFine_shouldRejectDuplicateFine() {
        FineServiceImpl fineService =
                new FineServiceImpl(fineRepository);

        UUID userId = UUID.randomUUID();
        UUID loanId = UUID.randomUUID();

        FineRequest request = new FineRequest(
                userId,
                loanId,
                2
        );

        when(fineRepository.existsByLoanId(loanId))
                .thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> fineService.createFine(request)
        );

        assertEquals(
                "El préstamo ya tiene una multa registrada",
                exception.getMessage()
        );

        verify(fineRepository, never())
                .save(any(Fine.class));
    }

    @Test
    void getFineById_shouldReturnFineWhenExists() {
        FineServiceImpl fineService =
                new FineServiceImpl(fineRepository);

        UUID fineId = UUID.randomUUID();

        Fine fine = fineEntity(
                fineId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(5000),
                FineStatus.PENDING
        );

        when(fineRepository.findById(fineId))
                .thenReturn(Optional.of(fine));

        ApiResponse<FineResponse> result =
                fineService.getFineById(fineId);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(fineId, result.data().id());
        assertEquals(
                BigDecimal.valueOf(5000),
                result.data().amount()
        );
        assertEquals(
                "Multa obtenida con éxito",
                result.message()
        );
    }

    @Test
    void getFineById_shouldThrowWhenMissing() {
        FineServiceImpl fineService =
                new FineServiceImpl(fineRepository);

        UUID fineId = UUID.randomUUID();

        when(fineRepository.findById(fineId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> fineService.getFineById(fineId)
        );

        assertEquals(
                "No se encontró la multa con id: " + fineId,
                exception.getMessage()
        );
    }

    @Test
    void getFinesByUserId_shouldReturnUserFines() {
        FineServiceImpl fineService =
                new FineServiceImpl(fineRepository);

        UUID userId = UUID.randomUUID();

        Fine firstFine = fineEntity(
                UUID.randomUUID(),
                userId,
                UUID.randomUUID(),
                BigDecimal.valueOf(2000),
                FineStatus.PENDING
        );

        Fine secondFine = fineEntity(
                UUID.randomUUID(),
                userId,
                UUID.randomUUID(),
                BigDecimal.valueOf(4000),
                FineStatus.PAID
        );

        when(fineRepository.findByUserId(userId))
                .thenReturn(List.of(firstFine, secondFine));

        ApiResponse<List<FineResponse>> result =
                fineService.getFinesByUserId(userId);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(2, result.data().size());
        assertEquals(userId, result.data().get(0).userId());
        assertEquals(
                "Multas obtenidas con éxito",
                result.message()
        );
    }

    @Test
    void getFinesByUserId_shouldThrowWhenEmpty() {
        FineServiceImpl fineService =
                new FineServiceImpl(fineRepository);

        UUID userId = UUID.randomUUID();

        when(fineRepository.findByUserId(userId))
                .thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> fineService.getFinesByUserId(userId)
        );

        assertEquals(
                "No se encontraron multas para el usuario: "
                        + userId,
                exception.getMessage()
        );
    }

    @Test
    void payFine_shouldChangeStatusToPaid() {
        FineServiceImpl fineService =
                new FineServiceImpl(fineRepository);

        UUID fineId = UUID.randomUUID();

        Fine pendingFine = fineEntity(
                fineId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(3000),
                FineStatus.PENDING
        );

        when(fineRepository.findById(fineId))
                .thenReturn(Optional.of(pendingFine));

        when(fineRepository.save(any(Fine.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        ApiResponse<FineResponse> result =
                fineService.payFine(fineId);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(
                FineStatus.PAID,
                result.data().status()
        );
        assertEquals(
                FineStatus.PAID,
                pendingFine.getStatus()
        );
        assertEquals(
                "Multa pagada con éxito",
                result.message()
        );

        verify(fineRepository).save(pendingFine);
    }

    @Test
    void payFine_shouldRejectAlreadyPaidFine() {
        FineServiceImpl fineService =
                new FineServiceImpl(fineRepository);

        UUID fineId = UUID.randomUUID();

        Fine paidFine = fineEntity(
                fineId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(3000),
                FineStatus.PAID
        );

        when(fineRepository.findById(fineId))
                .thenReturn(Optional.of(paidFine));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> fineService.payFine(fineId)
        );

        assertEquals(
                "La multa ya se encuentra pagada",
                exception.getMessage()
        );

        verify(fineRepository, never())
                .save(any(Fine.class));
    }

    @Test
    void countFines_shouldReturnTotalCount() {
        FineServiceImpl fineService =
                new FineServiceImpl(fineRepository);

        when(fineRepository.count())
                .thenReturn(10L);

        long result = fineService.countFines();

        assertEquals(10L, result);
        verify(fineRepository).count();
    }

    @Test
    void countPendingFines_shouldReturnPendingCount() {
        FineServiceImpl fineService =
                new FineServiceImpl(fineRepository);

        when(
                fineRepository.countByStatus(
                        FineStatus.PENDING
                )
        ).thenReturn(6L);

        long result = fineService.countPendingFines();

        assertEquals(6L, result);

        verify(fineRepository).countByStatus(
                FineStatus.PENDING
        );
    }

    @Test
    void countPaidFines_shouldReturnPaidCount() {
        FineServiceImpl fineService =
                new FineServiceImpl(fineRepository);

        when(
                fineRepository.countByStatus(
                        FineStatus.PAID
                )
        ).thenReturn(4L);

        long result = fineService.countPaidFines();

        assertEquals(4L, result);

        verify(fineRepository).countByStatus(
                FineStatus.PAID
        );
    }

    private Fine fineEntity(
            UUID fineId,
            UUID userId,
            UUID loanId,
            BigDecimal amount,
            FineStatus status
    ) {
        Fine fine = new Fine();

        fine.setId(fineId);
        fine.setUserId(userId);
        fine.setLoanId(loanId);
        fine.setAmount(amount);
        fine.setFineDate(LocalDate.now());
        fine.setStatus(status);

        return fine;
    }
}
