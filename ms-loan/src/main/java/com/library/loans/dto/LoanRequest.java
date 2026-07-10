package com.library.loans.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record LoanRequest(

        @NotNull(message = "El identificador del usuario es obligatorio")
        UUID userId,

        @NotNull(message = "El identificador del libro es obligatorio")
        UUID bookId

) {
}