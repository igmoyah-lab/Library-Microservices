package com.library.fines.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FineRequest(

        @NotNull(message = "El identificador del usuario es obligatorio")
        UUID userId,

        @NotNull(message = "El identificador del préstamo es obligatorio")
        UUID loanId,

        @Min(value = 1, message = "Los días de atraso deben ser mayores a cero")
        int delayedDays

) {
}