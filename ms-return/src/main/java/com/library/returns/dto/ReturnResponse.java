package com.library.returns.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ReturnResponse(
        UUID id,
        UUID loanId,
        LocalDate returnDate,
        boolean delayed
) {
}