package com.library.bff.services;
import org.springframework.stereotype.Service;

import com.library.bff.client.BookClient;
import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.BookRequest;
import com.library.bff.dto.BookResponse;
import com.library.bff.dto.BookSearchRequest;

import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    private final BookClient bookClient;

    public BookService(BookClient bookClient) {
        this.bookClient = bookClient;
    }

    public ApiResponse<List<BookResponse>> getAllBooks() {
        return bookClient.getAllBooks();
    }

    public ApiResponse<BookResponse> getBookById(UUID id) {
        return bookClient.getBookById(id);
    }

    public ApiResponse<List<BookResponse>> searchBooks(BookSearchRequest request) {
        return bookClient.searchBooks(request);
    }

    public ApiResponse<BookResponse> createBook(BookRequest request) {
        return bookClient.createBook(request);
    }

    public ApiResponse<BookResponse> updateBook(UUID id, BookRequest request) {
        return bookClient.updateBook(id, request);
    }

    public ApiResponse<Void> deleteBook(UUID id) {
        return bookClient.deleteBook(id);
    }
}