package com.library.books.controller;

import com.library.books.dto.ApiResponse;
import com.library.books.dto.BookDto;
import com.library.books.service.BookService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @Test
    void getAllBooks_shouldReturnOkResponse() {
        BookController bookController = new BookController(bookService);

        BookDto book = response(
                UUID.randomUUID(),
                "El Principito",
                "Antoine de Saint-Exupéry",
                "Literatura",
                "123456789"
        );

        ApiResponse<List<BookDto>> apiResponse = new ApiResponse<>(
                true,
                List.of(book),
                "Libros obtenidos correctamente"
        );

        when(bookService.getAllBooks()).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<List<BookDto>>> result = bookController.getAllBooks();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(true, result.getBody().success());
        assertEquals(1, result.getBody().data().size());
        assertEquals("El Principito", result.getBody().data().get(0).title());
    }

    @Test
    void getBookById_shouldReturnOkResponse() {
        BookController bookController = new BookController(bookService);

        BookDto book = response(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                "Programación",
                "111222333"
        );

        ApiResponse<BookDto> apiResponse = new ApiResponse<>(
                true,
                book,
                "Libro encontrado correctamente"
        );

        when(bookService.getBookById(any())).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<BookDto>> result = bookController.getBookById(UUID.randomUUID());

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(true, result.getBody().success());
        assertEquals("Clean Code", result.getBody().data().title());
    }

    @Test
    void createBook_shouldReturnCreatedResponse() {
        BookController bookController = new BookController(bookService);

        BookDto book = response(
                UUID.randomUUID(),
                "Harry Potter",
                "J. K. Rowling",
                "Fantasía",
                "987654321"
        );

        ApiResponse<BookDto> apiResponse = new ApiResponse<>(
                true,
                book,
                "Libro creado correctamente"
        );

        when(bookService.createBook(any())).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<BookDto>> result = bookController.createBook(book);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(true, result.getBody().success());
        assertEquals("Harry Potter", result.getBody().data().title());
    }

    @Test
    void deleteBook_shouldReturnNoContent() {
        BookController bookController = new BookController(bookService);

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                true,
                null,
                "Libro eliminado correctamente"
        );

        when(bookService.deleteBook(any())).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<Void>> result = bookController.deleteBook(UUID.randomUUID());

        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(bookService).deleteBook(any());
    }

    private BookDto response(UUID id, String title, String author, String category, String isbn) {
        return new BookDto(id, title, author, category, isbn);
    }
}