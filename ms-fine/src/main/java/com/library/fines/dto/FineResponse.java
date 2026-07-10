package com.library.fines.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.library.fines.entity.FineStatus;

public record FineResponse(
        UUID id,
        UUID userId,
        UUID loanId,
        BigDecimal amount,
        LocalDate fineDate,
        FineStatus status
) {
}