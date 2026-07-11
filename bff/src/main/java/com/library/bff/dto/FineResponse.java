package com.library.bff.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FineResponse(
        UUID id,
        UUID userId,
        UUID loanId,
        BigDecimal amount,
        LocalDate fineDate,
        String status
) {
}
