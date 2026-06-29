package com.library.bff.dto;

public record BookSearchRequest(
    String title,
    String author,
    String category,
    String isbn
) {
}