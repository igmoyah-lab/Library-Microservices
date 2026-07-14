package com.library.returns.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record ReturnRequest(

        @NotNull(message = "El identificador del préstamo es obligatorio")
        UUID loanId

) {
}