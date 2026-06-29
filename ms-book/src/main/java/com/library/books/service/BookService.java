package com.library.books.service;

import java.util.List;
import java.util.UUID;

import com.library.books.dto.ApiResponse;
import com.library.books.dto.BookDto;
import com.library.books.dto.BookSearchDto;


public interface BookService {
    
    ApiResponse<List<BookDto>> searchBooks(BookSearchDto searchDto);
    ApiResponse<BookDto> createBook(BookDto bookDto);
    ApiResponse<BookDto> getBookById(UUID id);
    ApiResponse<BookDto> updateBook(UUID id, BookDto bookDto);
    ApiResponse<Void> deleteBook(UUID id);
    ApiResponse<List<BookDto>> getAllBooks();
} 
