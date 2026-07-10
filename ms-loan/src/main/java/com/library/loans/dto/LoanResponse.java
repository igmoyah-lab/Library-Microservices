package com.library.loans.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.library.loans.entity.LoanStatus;

public record LoanResponse(
        UUID id,
        UUID userId,
        UUID bookId,
        LocalDate loanDate,
        LocalDate dueDate,
        LoanStatus status
) {
}
