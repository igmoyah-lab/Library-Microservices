package com.library.bff.dto;

import jakarta.validation.constraints.NotBlank;

public record BookRequest(
    @NotBlank String title,
    @NotBlank String author,
    @NotBlank String category,
    @NotBlank String isbn
) {
}