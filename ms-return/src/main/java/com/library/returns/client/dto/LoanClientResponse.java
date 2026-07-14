package com.library.returns.client.dto;

import java.time.LocalDate;
import java.util.UUID;

public record LoanClientResponse(
        UUID id,
        UUID userId,
        UUID bookId,
        LocalDate loanDate,
        LocalDate dueDate,
        String status
) {
}