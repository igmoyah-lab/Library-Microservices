package com.library.books.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record BookDto(
    UUID id,
    @NotBlank String title,
    @NotBlank String author,
    @NotBlank String category,
    @NotBlank String isbn 
) {
 
}
