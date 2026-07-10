package com.library.notifications.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationRequest(

        @NotNull(message = "El identificador del usuario es obligatorio")
        UUID userId,

        @NotBlank(message = "El mensaje es obligatorio")
        @Size(
                max = 500,
                message = "El mensaje no puede superar los 500 caracteres"
        )
        String message

) {
}