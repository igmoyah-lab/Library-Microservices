package com.library.bff.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.bff.client.BookClient;
import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.BookRequest;
import com.library.bff.dto.BookResponse;
import com.library.bff.dto.BookSearchRequest;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookClient bookClient;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookClient);
    }

    @Test
    void getAllBooks_shouldDelegateToBookClient() {
        BookResponse book = new BookResponse(UUID.randomUUID(), "El Principito", "Antoine", "Literatura", "123");
        ApiResponse<List<BookResponse>> expectedResponse = new ApiResponse<>(true, List.of(book), "Libros encontrados");

        when(bookClient.getAllBooks()).thenReturn(expectedResponse);

        ApiResponse<List<BookResponse>> response = bookService.getAllBooks();

        assertEquals(expectedResponse, response);
        verify(bookClient).getAllBooks();
    }

    @Test
    void getBookById_shouldDelegateToBookClient() {
        UUID id = UUID.randomUUID();
        BookResponse book = new BookResponse(id, "El Principito", "Antoine", "Literatura", "123");
        ApiResponse<BookResponse> expectedResponse = new ApiResponse<>(true, book, "Libro encontrado");

        when(bookClient.getBookById(id)).thenReturn(expectedResponse);

        ApiResponse<BookResponse> response = bookService.getBookById(id);

        assertEquals(expectedResponse, response);
        verify(bookClient).getBookById(id);
    }

    @Test
    void searchBooks_shouldDelegateToBookClient() {
        BookSearchRequest request = new BookSearchRequest("El Principito", null, null, null);
        ApiResponse<List<BookResponse>> expectedResponse = new ApiResponse<>(true, List.of(), "Búsqueda realizada");

        when(bookClient.searchBooks(request)).thenReturn(expectedResponse);

        ApiResponse<List<BookResponse>> response = bookService.searchBooks(request);

        assertEquals(expectedResponse, response);
        verify(bookClient).searchBooks(request);
    }

    @Test
    void createBook_shouldDelegateToBookClient() {
        BookRequest request = new BookRequest("El Principito", "Antoine", "Literatura", "123");
        BookResponse book = new BookResponse(UUID.randomUUID(), "El Principito", "Antoine", "Literatura", "123");
        ApiResponse<BookResponse> expectedResponse = new ApiResponse<>(true, book, "Libro creado");

        when(bookClient.createBook(request)).thenReturn(expectedResponse);

        ApiResponse<BookResponse> response = bookService.createBook(request);

        assertEquals(expectedResponse, response);
        verify(bookClient).createBook(request);
    }

    @Test
    void updateBook_shouldDelegateToBookClient() {
        UUID id = UUID.randomUUID();
        BookRequest request = new BookRequest("Libro editado", "Autor", "Categoría", "456");
        BookResponse book = new BookResponse(id, "Libro editado", "Autor", "Categoría", "456");
        ApiResponse<BookResponse> expectedResponse = new ApiResponse<>(true, book, "Libro actualizado");

        when(bookClient.updateBook(id, request)).thenReturn(expectedResponse);

        ApiResponse<BookResponse> response = bookService.updateBook(id, request);

        assertEquals(expectedResponse, response);
        verify(bookClient).updateBook(id, request);
    }

    @Test
    void deleteBook_shouldDelegateToBookClient() {
        UUID id = UUID.randomUUID();
        ApiResponse<Void> expectedResponse = new ApiResponse<>(true, null, "Libro eliminado");

        when(bookClient.deleteBook(id)).thenReturn(expectedResponse);

        ApiResponse<Void> response = bookService.deleteBook(id);

        assertEquals(expectedResponse, response);
        verify(bookClient).deleteBook(id);
    }
}
