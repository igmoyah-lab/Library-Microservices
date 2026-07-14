package com.library.fines.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.library.fines.entity.Fine;
import com.library.fines.entity.FineStatus;

@DataJpaTest
@ActiveProfiles("test")
class FineRepositoryTest {

    @Autowired
    private FineRepository fineRepository;

    private UUID userId;
    private UUID otherUserId;
    private UUID loanId;
    private UUID otherLoanId;

    @BeforeEach
    void setUp() {
        fineRepository.deleteAll();

        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        loanId = UUID.randomUUID();
        otherLoanId = UUID.randomUUID();
    }

    @Test
    void findByUserId_shouldReturnFinesBelongingToUser() {
        Fine firstFine = createFine(
                userId,
                loanId,
                FineStatus.PENDING
        );

        Fine secondFine = createFine(
                userId,
                UUID.randomUUID(),
                FineStatus.PAID
        );

        Fine fineFromOtherUser = createFine(
                otherUserId,
                otherLoanId,
                FineStatus.PENDING
        );

        fineRepository.saveAllAndFlush(
                List.of(
                        firstFine,
                        secondFine,
                        fineFromOtherUser
                )
        );

        List<Fine> result = fineRepository.findByUserId(userId);

        assertEquals(2, result.size());

        assertTrue(
                result.stream()
                        .allMatch(fine -> fine.getUserId().equals(userId))
        );
    }

    @Test
    void findByUserId_shouldReturnEmptyListWhenUserHasNoFines() {
        Fine fine = createFine(
                userId,
                loanId,
                FineStatus.PENDING
        );

        fineRepository.saveAndFlush(fine);

        List<Fine> result =
                fineRepository.findByUserId(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByLoanId_shouldReturnTrueWhenLoanHasFine() {
        Fine fine = createFine(
                userId,
                loanId,
                FineStatus.PENDING
        );

        fineRepository.saveAndFlush(fine);

        boolean result = fineRepository.existsByLoanId(loanId);

        assertTrue(result);
    }

    @Test
    void existsByLoanId_shouldReturnFalseWhenLoanHasNoFine() {
        Fine fine = createFine(
                userId,
                loanId,
                FineStatus.PENDING
        );

        fineRepository.saveAndFlush(fine);

        boolean result =
                fineRepository.existsByLoanId(UUID.randomUUID());

        assertFalse(result);
    }

    @Test
    void countByStatus_shouldReturnPendingFinesCount() {
        Fine firstFine = createFine(
                userId,
                UUID.randomUUID(),
                FineStatus.PENDING
        );

        Fine secondFine = createFine(
                otherUserId,
                UUID.randomUUID(),
                FineStatus.PENDING
        );

        Fine paidFine = createFine(
                UUID.randomUUID(),
                UUID.randomUUID(),
                FineStatus.PAID
        );

        fineRepository.saveAllAndFlush(
                List.of(
                        firstFine,
                        secondFine,
                        paidFine
                )
        );

        long result =
                fineRepository.countByStatus(FineStatus.PENDING);

        assertEquals(2L, result);
    }

    @Test
    void countByStatus_shouldReturnPaidFinesCount() {
        Fine firstPaidFine = createFine(
                userId,
                UUID.randomUUID(),
                FineStatus.PAID
        );

        Fine secondPaidFine = createFine(
                otherUserId,
                UUID.randomUUID(),
                FineStatus.PAID
        );

        Fine pendingFine = createFine(
                UUID.randomUUID(),
                UUID.randomUUID(),
                FineStatus.PENDING
        );

        fineRepository.saveAllAndFlush(
                List.of(
                        firstPaidFine,
                        secondPaidFine,
                        pendingFine
                )
        );

        long result =
                fineRepository.countByStatus(FineStatus.PAID);

        assertEquals(2L, result);
    }

    @Test
    void countByStatus_shouldReturnZeroWhenNoFinesMatchStatus() {
        Fine fine = createFine(
                userId,
                loanId,
                FineStatus.PENDING
        );

        fineRepository.saveAndFlush(fine);

        long result =
                fineRepository.countByStatus(FineStatus.PAID);

        assertEquals(0L, result);
    }

    private Fine createFine(
            UUID userId,
            UUID loanId,
            FineStatus status
    ) {
        Fine fine = new Fine();

        fine.setUserId(userId);
        fine.setLoanId(loanId);
        fine.setAmount(new BigDecimal("15000.00"));
        fine.setFineDate(LocalDate.of(2026, 7, 12));
        fine.setStatus(status);

        return fine;
    }
}