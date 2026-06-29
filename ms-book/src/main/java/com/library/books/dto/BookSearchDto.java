package com.library.books.dto;

public record BookSearchDto(
    String title,
    String author,
    String category,
    String isbn
    
) {

}
