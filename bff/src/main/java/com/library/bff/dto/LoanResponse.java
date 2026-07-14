package com.library.bff.dto;

import java.time.LocalDate;
import java.util.UUID;

public record LoanResponse(
        UUID id,
        UUID userId,
        UUID bookId,
        LocalDate loanDate,
        LocalDate dueDate,
        String status
) {
}
