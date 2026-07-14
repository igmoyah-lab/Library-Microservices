package com.library.books.service;

import com.library.books.dto.ApiResponse;
import com.library.books.dto.BookDto;
import com.library.books.dto.BookSearchDto;
import com.library.books.entity.Book;
import com.library.books.exception.ResourceNotFoundException;
import com.library.books.repository.BookRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Test
    void getBookById_shouldReturnBookWhenExists() {
        BookServiceImpl bookService = new BookServiceImpl(bookRepository);

        UUID id = UUID.randomUUID();

        Book book = entity(
                id,
                "El Principito",
                "Antoine de Saint-Exupéry",
                "Literatura",
                "123456789"
        );

        when(bookRepository.findById(id))
                .thenReturn(Optional.of(book));

        ApiResponse<BookDto> result =
                bookService.getBookById(id);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(id, result.data().id());
        assertEquals(
                "El Principito",
                result.data().title()
        );
        assertTrue(result.data().available());
        assertEquals(
                "Libro encontrado con éxito",
                result.message()
        );
    }

    @Test
    void getBookById_shouldThrowExceptionWhenMissing() {
        BookServiceImpl bookService =
                new BookServiceImpl(bookRepository);

        UUID id = UUID.randomUUID();

        when(bookRepository.findById(id))
                .thenReturn(Optional.empty());

        ResourceNotFoundException result = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.getBookById(id)
        );

        assertEquals(
                "Libro no encontrado con id: " + id,
                result.getMessage()
        );
    }

    @Test
    void getAllBooks_shouldReturnListOfBooks() {
        BookServiceImpl bookService =
                new BookServiceImpl(bookRepository);

        Book book1 = entity(
                UUID.randomUUID(),
                "El Principito",
                "Antoine de Saint-Exupéry",
                "Literatura",
                "123456789"
        );

        Book book2 = entity(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                "Programación",
                "987654321"
        );

        when(bookRepository.findAll())
                .thenReturn(List.of(book1, book2));

        ApiResponse<List<BookDto>> result =
                bookService.getAllBooks();

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(2, result.data().size());
        assertEquals(
                "El Principito",
                result.data().get(0).title()
        );
        assertTrue(result.data().get(0).available());
        assertEquals(
                "Libros obtenidos con éxito",
                result.message()
        );
    }

    @Test
    void searchBooks_shouldReturnBooksByTitle() {
        BookServiceImpl bookService =
                new BookServiceImpl(bookRepository);

        Book book1 = entity(
                UUID.randomUUID(),
                "El Principito",
                "Antoine de Saint-Exupéry",
                "Literatura",
                "123456789"
        );

        Book book2 = entity(
                UUID.randomUUID(),
                "Clean Code",
                "Robert C. Martin",
                "Programación",
                "987654321"
        );

        when(bookRepository.findAll())
                .thenReturn(List.of(book1, book2));

        BookSearchDto searchDto = new BookSearchDto(
                "principito",
                null,
                null,
                null
        );

        ApiResponse<List<BookDto>> result =
                bookService.searchBooks(searchDto);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(1, result.data().size());
        assertEquals(
                "El Principito",
                result.data().get(0).title()
        );
        assertEquals(
                "Libros encontrados con éxito",
                result.message()
        );
    }

    @Test
    void searchBooks_shouldReturnEmptyListWhenNoMatches() {
        BookServiceImpl bookService =
                new BookServiceImpl(bookRepository);

        Book book = entity(
                UUID.randomUUID(),
                "El Principito",
                "Antoine de Saint-Exupéry",
                "Literatura",
                "123456789"
        );

        when(bookRepository.findAll())
                .thenReturn(List.of(book));

        BookSearchDto searchDto = new BookSearchDto(
                "no existe",
                null,
                null,
                null
        );

        ApiResponse<List<BookDto>> result =
                bookService.searchBooks(searchDto);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertTrue(result.data().isEmpty());
        assertEquals(
                "No se encontraron libros con los criterios proporcionados",
                result.message()
        );
    }

    @Test
    void createBook_shouldSaveAndReturnDto() {
        BookServiceImpl bookService =
                new BookServiceImpl(bookRepository);

        UUID id = UUID.randomUUID();

        BookDto request = new BookDto(
                null,
                "Harry Potter",
                "J. K. Rowling",
                "Fantasía",
                "111222333"
        );

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> {
                    Book bookToSave = invocation.getArgument(0);
                    bookToSave.setId(id);
                    return bookToSave;
                });

        ApiResponse<BookDto> result =
                bookService.createBook(request);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(id, result.data().id());
        assertEquals(
                "Harry Potter",
                result.data().title()
        );
        assertTrue(result.data().available());
        assertEquals(
                "Libro creado con éxito",
                result.message()
        );

        ArgumentCaptor<Book> captor =
                ArgumentCaptor.forClass(Book.class);

        verify(bookRepository).save(captor.capture());

        Book savedBook = captor.getValue();

        assertEquals(
                "Harry Potter",
                savedBook.getTitle()
        );
        assertEquals(
                "J. K. Rowling",
                savedBook.getAuthor()
        );
        assertEquals(
                "Fantasía",
                savedBook.getCategory()
        );
        assertEquals(
                "111222333",
                savedBook.getIsbn()
        );
        assertTrue(savedBook.isAvailable());
    }

    @Test
    void updateBook_shouldUpdateAndReturnDtoWhenExists() {
        BookServiceImpl bookService =
                new BookServiceImpl(bookRepository);

        UUID id = UUID.randomUUID();

        Book existingBook = entity(
                id,
                "Libro Antiguo",
                "Autor Antiguo",
                "Antigua",
                "000000"
        );

        existingBook.setAvailable(false);

        BookDto request = new BookDto(
                id,
                "Libro Actualizado",
                "Autor Nuevo",
                "Nueva Categoría",
                "999999"
        );

        when(bookRepository.findById(id))
                .thenReturn(Optional.of(existingBook));

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        ApiResponse<BookDto> result =
                bookService.updateBook(id, request);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(
                "Libro Actualizado",
                result.data().title()
        );
        assertEquals(
                "Autor Nuevo",
                result.data().author()
        );
        assertEquals(
                "Nueva Categoría",
                result.data().category()
        );
        assertEquals(
                "999999",
                result.data().isbn()
        );

        // La actualización general no debe cambiar disponibilidad.
        assertFalse(result.data().available());

        assertEquals(
                "Libro actualizado con éxito",
                result.message()
        );

        verify(bookRepository).save(existingBook);
    }

    @Test
    void updateBook_shouldThrowExceptionWhenMissing() {
        BookServiceImpl bookService =
                new BookServiceImpl(bookRepository);

        UUID id = UUID.randomUUID();

        BookDto request = new BookDto(
                id,
                "Libro Actualizado",
                "Autor Nuevo",
                "Nueva Categoría",
                "999999"
        );

        when(bookRepository.findById(id))
                .thenReturn(Optional.empty());

        ResourceNotFoundException result = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.updateBook(id, request)
        );

        assertEquals(
                "Libro no encontrado con id: " + id,
                result.getMessage()
        );
    }

    @Test
    void updateAvailability_shouldMarkBookAsUnavailable() {
        BookServiceImpl bookService =
                new BookServiceImpl(bookRepository);

        UUID id = UUID.randomUUID();

        Book existingBook = entity(
                id,
                "El Principito",
                "Antoine de Saint-Exupéry",
                "Literatura",
                "123456789"
        );

        assertTrue(existingBook.isAvailable());

        when(bookRepository.findById(id))
                .thenReturn(Optional.of(existingBook));

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        ApiResponse<BookDto> result =
                bookService.updateAvailability(id, false);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertFalse(result.data().available());
        assertFalse(existingBook.isAvailable());
        assertEquals(
                "Libro marcado como no disponible",
                result.message()
        );

        verify(bookRepository).save(existingBook);
    }

    @Test
    void updateAvailability_shouldMarkBookAsAvailable() {
        BookServiceImpl bookService =
                new BookServiceImpl(bookRepository);

        UUID id = UUID.randomUUID();

        Book existingBook = entity(
                id,
                "Clean Code",
                "Robert C. Martin",
                "Programación",
                "987654321"
        );

        existingBook.setAvailable(false);

        when(bookRepository.findById(id))
                .thenReturn(Optional.of(existingBook));

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        ApiResponse<BookDto> result =
                bookService.updateAvailability(id, true);

        assertTrue(result.success());
        assertNotNull(result.data());
        assertTrue(result.data().available());
        assertTrue(existingBook.isAvailable());
        assertEquals(
                "Libro marcado como disponible",
                result.message()
        );

        verify(bookRepository).save(existingBook);
    }

    @Test
    void updateAvailability_shouldThrowExceptionWhenMissing() {
        BookServiceImpl bookService =
                new BookServiceImpl(bookRepository);

        UUID id = UUID.randomUUID();

        when(bookRepository.findById(id))
                .thenReturn(Optional.empty());

        ResourceNotFoundException result = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.updateAvailability(id, false)
        );

        assertEquals(
                "Libro no encontrado con id: " + id,
                result.getMessage()
        );
    }

    @Test
    void deleteBook_shouldDeleteWhenExists() {
        BookServiceImpl bookService =
                new BookServiceImpl(bookRepository);

        UUID id = UUID.randomUUID();

        Book book = entity(
                id,
                "Clean Code",
                "Robert C. Martin",
                "Programación",
                "987654321"
        );

        when(bookRepository.findById(id))
                .thenReturn(Optional.of(book));

        ApiResponse<Void> result =
                bookService.deleteBook(id);

        assertTrue(result.success());
        assertEquals(
                "Libro eliminado con éxito",
                result.message()
        );

        verify(bookRepository).delete(book);
    }

    @Test
    void deleteBook_shouldThrowExceptionWhenMissing() {
        BookServiceImpl bookService =
                new BookServiceImpl(bookRepository);

        UUID id = UUID.randomUUID();

        when(bookRepository.findById(id))
                .thenReturn(Optional.empty());

        ResourceNotFoundException result = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.deleteBook(id)
        );

        assertEquals(
                "Libro no encontrado con id: " + id,
                result.getMessage()
        );
    }

    private Book entity(
            UUID id,
            String title,
            String author,
            String category,
            String isbn
    ) {
        Book book = new Book();

        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setIsbn(isbn);
        book.setAvailable(true);

        return book;
    }
}