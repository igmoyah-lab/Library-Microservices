package com.library.bff.dto;

import java.util.UUID;

public record BookResponse(
    UUID id,
    String title,
    String author,
    String category,
    String isbn
) {
}