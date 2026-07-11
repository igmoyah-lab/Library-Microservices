package com.library.books.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.books.dto.ApiResponse;
import com.library.books.dto.BookDto;
import com.library.books.dto.BookSearchDto;
import com.library.books.entity.Book;
import com.library.books.exception.ResourceNotFoundException;
import com.library.books.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Obtiene un libro mediante su identificador.
     *
     * @param id identificador del libro
     * @return respuesta con el libro encontrado
     * @throws ResourceNotFoundException si el libro no existe
     */
    @Override
    public ApiResponse<BookDto> getBookById(UUID id) {
        Book book = findBookById(id);

        return new ApiResponse<>(
                true,
                mapToDto(book),
                "Libro encontrado con éxito"
        );
    }

    /**
     * Busca libros utilizando los criterios recibidos.
     *
     * @param searchDto criterios de búsqueda
     * @return respuesta con los libros encontrados
     */
    @Override
    public ApiResponse<List<BookDto>> searchBooks(BookSearchDto searchDto) {
        List<BookDto> books = bookRepository.findAll()
                .stream()
                .filter(book ->
                        searchDto.title() == null
                                || searchDto.title().isBlank()
                                || book.getTitle().toLowerCase()
                                        .contains(searchDto.title().toLowerCase())
                )
                .filter(book ->
                        searchDto.author() == null
                                || searchDto.author().isBlank()
                                || book.getAuthor().toLowerCase()
                                        .contains(searchDto.author().toLowerCase())
                )
                .filter(book ->
                        searchDto.category() == null
                                || searchDto.category().isBlank()
                                || book.getCategory().toLowerCase()
                                        .contains(searchDto.category().toLowerCase())
                )
                .filter(book ->
                        searchDto.isbn() == null
                                || searchDto.isbn().isBlank()
                                || book.getIsbn().toLowerCase()
                                        .contains(searchDto.isbn().toLowerCase())
                )
                .map(this::mapToDto)
                .toList();

        String message = books.isEmpty()
                ? "No se encontraron libros con los criterios proporcionados"
                : "Libros encontrados con éxito";

        return new ApiResponse<>(
                true,
                books,
                message
        );
    }

    /**
     * Crea un libro y lo establece inicialmente como disponible.
     *
     * @param bookDto datos del libro
     * @return respuesta con el libro creado
     */
    @Override
    public ApiResponse<BookDto> createBook(BookDto bookDto) {
        Book book = mapToEntity(bookDto);

        // Todo libro nuevo comienza disponible.
        book.setAvailable(true);

        Book savedBook = bookRepository.save(book);

        return new ApiResponse<>(
                true,
                mapToDto(savedBook),
                "Libro creado con éxito"
        );
    }

    /**
     * Actualiza los datos generales de un libro sin modificar
     * su estado de disponibilidad.
     *
     * @param id identificador del libro
     * @param bookDto nuevos datos del libro
     * @return respuesta con el libro actualizado
     * @throws ResourceNotFoundException si el libro no existe
     */
    @Override
    public ApiResponse<BookDto> updateBook(UUID id, BookDto bookDto) {
        Book existingBook = findBookById(id);

        existingBook.setTitle(bookDto.title());
        existingBook.setAuthor(bookDto.author());
        existingBook.setCategory(bookDto.category());
        existingBook.setIsbn(bookDto.isbn());

        Book updatedBook = bookRepository.save(existingBook);

        return new ApiResponse<>(
                true,
                mapToDto(updatedBook),
                "Libro actualizado con éxito"
        );
    }

    /**
     * Modifica únicamente el estado de disponibilidad de un libro.
     *
     * @param id identificador del libro
     * @param available nuevo estado de disponibilidad
     * @return respuesta con el libro actualizado
     * @throws ResourceNotFoundException si el libro no existe
     */
    @Override
    public ApiResponse<BookDto> updateAvailability(
            UUID id,
            boolean available
    ) {
        Book existingBook = findBookById(id);

        existingBook.setAvailable(available);

        Book updatedBook = bookRepository.save(existingBook);

        String message = available
                ? "Libro marcado como disponible"
                : "Libro marcado como no disponible";

        return new ApiResponse<>(
                true,
                mapToDto(updatedBook),
                message
        );
    }

    /**
     * Elimina un libro mediante su identificador.
     *
     * @param id identificador del libro
     * @return respuesta sin contenido
     * @throws ResourceNotFoundException si el libro no existe
     */
    @Override
    public ApiResponse<Void> deleteBook(UUID id) {
        Book existingBook = findBookById(id);

        bookRepository.delete(existingBook);

        return new ApiResponse<>(
                true,
                null,
                "Libro eliminado con éxito"
        );
    }

    /**
     * Obtiene todos los libros registrados.
     *
     * @return respuesta con la lista de libros
     */
    @Override
    public ApiResponse<List<BookDto>> getAllBooks() {
        List<BookDto> bookDtos = bookRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();

        return new ApiResponse<>(
                true,
                bookDtos,
                "Libros obtenidos con éxito"
        );
    }

    /**
     * Busca internamente un libro mediante su identificador.
     *
     * @param id identificador del libro
     * @return entidad encontrada
     * @throws ResourceNotFoundException si el libro no existe
     */
    private Book findBookById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Libro no encontrado con id: " + id
                        )
                );
    }

    /**
     * Convierte una entidad Book en BookDto.
     *
     * @param book entidad del libro
     * @return datos convertidos a DTO
     */
    private BookDto mapToDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getIsbn(),
                book.isAvailable()
        );
    }

    /**
     * Convierte un BookDto en una entidad Book.
     *
     * @param dto datos del libro
     * @return entidad creada
     */
    private Book mapToEntity(BookDto dto) {
        Book book = new Book();

        book.setId(dto.id());
        book.setTitle(dto.title());
        book.setAuthor(dto.author());
        book.setCategory(dto.category());
        book.setIsbn(dto.isbn());

        return book;
    }
}
