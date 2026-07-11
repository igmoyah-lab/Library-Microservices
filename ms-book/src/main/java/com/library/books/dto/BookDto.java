package com.library.books.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record BookDto(
        UUID id,

        @NotBlank(message = "El título es obligatorio")
        String title,

        @NotBlank(message = "El autor es obligatorio")
        String author,

        @NotBlank(message = "La categoría es obligatoria")
        String category,

        @NotBlank(message = "El ISBN es obligatorio")
        String isbn,

        Boolean available
) {

    /**
     * Permite crear un BookDto sin indicar disponibilidad.
     * En ese caso, el libro queda disponible por defecto.
     *
     * @param id identificador del libro
     * @param title título del libro
     * @param author autor del libro
     * @param category categoría del libro
     * @param isbn código ISBN
     */
    public BookDto(
            UUID id,
            String title,
            String author,
            String category,
            String isbn
    ) {
        this(id, title, author, category, isbn, true);
    }

    /**
     * Establece disponibilidad verdadera cuando no viene informada.
     */
    public BookDto {
        available = available == null ? true : available;
    }
}