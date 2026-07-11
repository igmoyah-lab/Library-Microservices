package com.library.books.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.books.dto.ApiResponse;
import com.library.books.dto.BookDto;
import com.library.books.dto.BookSearchDto;
import com.library.books.service.BookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookDto>>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookDto>>> searchBooks(
            BookSearchDto searchDto
    ) {
        return ResponseEntity.ok(bookService.searchBooks(searchDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> getBookById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookDto>> createBook(
            @Valid @RequestBody BookDto bookDto
    ) {
        return ResponseEntity.ok(bookService.createBook(bookDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> updateBook(
            @PathVariable UUID id,
            @Valid @RequestBody BookDto bookDto
    ) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDto));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<BookDto>> updateAvailability(
            @PathVariable UUID id,
            @RequestParam boolean available
    ) {
        return ResponseEntity.ok(
                bookService.updateAvailability(id, available)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(bookService.deleteBook(id));
    }
}
