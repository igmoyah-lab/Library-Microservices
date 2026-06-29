package com.library.bff.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.BookRequest;
import com.library.bff.dto.BookResponse;
import com.library.bff.dto.BookSearchRequest;
import com.library.bff.services.BookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ApiResponse<List<BookResponse>> getAll() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public ApiResponse<BookResponse> getById(@PathVariable UUID id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/search")
    public ApiResponse<List<BookResponse>> search(@ModelAttribute BookSearchRequest request) {
        return bookService.searchBooks(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BookResponse> create(@Valid @RequestBody BookRequest request) {
        return bookService.createBook(request);
    }

    @PutMapping("/{id}")
    public ApiResponse<BookResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody BookRequest request
    ) {
        return bookService.updateBook(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        return bookService.deleteBook(id);
    }
}