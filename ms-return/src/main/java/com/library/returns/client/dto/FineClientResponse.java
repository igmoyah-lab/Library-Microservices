package com.library.returns.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FineClientResponse(
        UUID id,
        UUID userId,
        UUID loanId,
        BigDecimal amount,
        LocalDate fineDate,
        String status
) {
}