package com.library.books.service;

import java.util.List;
import java.util.UUID;

import com.library.books.dto.ApiResponse;
import com.library.books.dto.BookDto;
import com.library.books.dto.BookSearchDto;

public interface BookService {

    /**
     * Busca libros según los criterios proporcionados.
     *
     * @param searchDto criterios de búsqueda
     * @return respuesta con los libros encontrados
     */
    ApiResponse<List<BookDto>> searchBooks(BookSearchDto searchDto);

    /**
     * Crea un libro y lo establece como disponible.
     *
     * @param bookDto datos del libro
     * @return respuesta con el libro creado
     */
    ApiResponse<BookDto> createBook(BookDto bookDto);

    /**
     * Obtiene un libro mediante su identificador.
     *
     * @param id identificador del libro
     * @return respuesta con el libro encontrado
     */
    ApiResponse<BookDto> getBookById(UUID id);

    /**
     * Actualiza los datos generales de un libro.
     *
     * @param id identificador del libro
     * @param bookDto nuevos datos del libro
     * @return respuesta con el libro actualizado
     */
    ApiResponse<BookDto> updateBook(UUID id, BookDto bookDto);

    /**
     * Modifica la disponibilidad de un libro.
     *
     * @param id identificador del libro
     * @param available nuevo estado de disponibilidad
     * @return respuesta con el libro actualizado
     */
    ApiResponse<BookDto> updateAvailability(UUID id, boolean available);

    /**
     * Elimina un libro mediante su identificador.
     *
     * @param id identificador del libro
     * @return respuesta sin contenido
     */
    ApiResponse<Void> deleteBook(UUID id);

    /**
     * Obtiene todos los libros registrados.
     *
     * @return respuesta con la lista de libros
     */
    ApiResponse<List<BookDto>> getAllBooks();
}
