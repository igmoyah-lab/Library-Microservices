package com.library.fines.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.library.fines.entity.FineStatus;

class FineResponseTest {

    private UUID fineId;
    private UUID userId;
    private UUID loanId;
    private BigDecimal amount;
    private LocalDate fineDate;

    @BeforeEach
    void setUp() {
        fineId = UUID.randomUUID();
        userId = UUID.randomUUID();
        loanId = UUID.randomUUID();
        amount = new BigDecimal("15000.00");
        fineDate = LocalDate.of(2026, 7, 12);
    }

    @Test
    void shouldCreateFineResponseWithCorrectValues() {
        FineResponse response = new FineResponse(
                fineId,
                userId,
                loanId,
                amount,
                fineDate,
                FineStatus.PENDING
        );

        assertEquals(fineId, response.id());
        assertEquals(userId, response.userId());
        assertEquals(loanId, response.loanId());
        assertEquals(amount, response.amount());
        assertEquals(fineDate, response.fineDate());
        assertEquals(FineStatus.PENDING, response.status());
    }

    @Test
    void shouldCreatePaidFineResponse() {
        FineResponse response = new FineResponse(
                fineId,
                userId,
                loanId,
                amount,
                fineDate,
                FineStatus.PAID
        );

        assertEquals(FineStatus.PAID, response.status());
    }

    @Test
    void equalFineResponses_shouldBeEqual() {
        FineResponse firstResponse = new FineResponse(
                fineId,
                userId,
                loanId,
                amount,
                fineDate,
                FineStatus.PENDING
        );

        FineResponse secondResponse = new FineResponse(
                fineId,
                userId,
                loanId,
                amount,
                fineDate,
                FineStatus.PENDING
        );

        assertEquals(firstResponse, secondResponse);
        assertEquals(firstResponse.hashCode(), secondResponse.hashCode());
    }

    @Test
    void fineResponsesWithDifferentValues_shouldNotBeEqual() {
        FineResponse firstResponse = new FineResponse(
                fineId,
                userId,
                loanId,
                amount,
                fineDate,
                FineStatus.PENDING
        );

        FineResponse secondResponse = new FineResponse(
                UUID.randomUUID(),
                userId,
                loanId,
                new BigDecimal("20000.00"),
                fineDate,
                FineStatus.PAID
        );

        assertNotEquals(firstResponse, secondResponse);
    }

    @Test
    void toString_shouldContainFineResponseValues() {
        FineResponse response = new FineResponse(
                fineId,
                userId,
                loanId,
                amount,
                fineDate,
                FineStatus.PENDING
        );

        String result = response.toString();

        assertTrue(result.contains(fineId.toString()));
        assertTrue(result.contains(userId.toString()));
        assertTrue(result.contains(loanId.toString()));
        assertTrue(result.contains("15000.00"));
        assertTrue(result.contains("2026-07-12"));
        assertTrue(result.contains("PENDING"));
    }
}