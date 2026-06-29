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

   
    //OBTENER UN LIBRO POR ID
    @Override
    public ApiResponse<BookDto> getBookById(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));
        return new ApiResponse<>(true, mapToDto(book), "Libro encontrado con éxito");
    }

    //BUSCAR LIBROS
    @Override
    public ApiResponse<List<BookDto>> searchBooks(BookSearchDto searchDto) {
        List<BookDto> books = bookRepository.findAll()
                .stream()
                .filter(book -> searchDto.title() == null || searchDto.title().isBlank()
                        || book.getTitle().toLowerCase().contains(searchDto.title().toLowerCase()))
                .filter(book -> searchDto.author() == null || searchDto.author().isBlank()
                        || book.getAuthor().toLowerCase().contains(searchDto.author().toLowerCase()))
                .filter(book -> searchDto.category() == null || searchDto.category().isBlank()
                        || book.getCategory().toLowerCase().contains(searchDto.category().toLowerCase()))
                .filter(book -> searchDto.isbn() == null || searchDto.isbn().isBlank()
                        || book.getIsbn().toLowerCase().contains(searchDto.isbn().toLowerCase()))
                .map(this::mapToDto)
                .toList();

        return new ApiResponse<>(
                true,
                books,
                books.isEmpty() ? "No se encontraron libros con los criterios de búsqueda proporcionados" : "Libros encontrados con éxito"
        );
    }

    //CREAR UN LIBRO
    @Override
    public ApiResponse<BookDto> createBook(BookDto bookDto) {

        Book book = mapToEntity(bookDto);

        Book savedBook = bookRepository.save(book);

        return new ApiResponse<>(
            true,
            mapToDto(savedBook),
            "Libro creado con éxito"
        );
    }

    //ACTUALIZAR UN LIBRO
    @Override
    public ApiResponse<BookDto> updateBook(UUID id, BookDto bookDto) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
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

    //ELIMINAR UN LIBRO
    @Override
    public ApiResponse<Void> deleteBook(UUID id) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        bookRepository.delete(existingBook);
        return new ApiResponse<>(true, null, "Libro eliminado con éxito");
    }

    //OBTENER TODOS LOS LIBROS
    @Override
    public ApiResponse<List<BookDto>> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        List<BookDto> bookDtos = books.stream().map(this::mapToDto).toList();
        return new ApiResponse<>(true, bookDtos, "Libros obtenidos con éxito");
    }

    private BookDto mapToDto(Book book) {
        return new BookDto(book.getId(), book.getTitle(), book.getAuthor(), book.getCategory(), book.getIsbn()
        );
    }

    private Book mapToEntity(BookDto dto) {
        Book book = new Book();
        book .setId(dto.id());
        book.setTitle(dto.title());
        book.setAuthor(dto.author());
        book.setCategory(dto.category());
        book.setIsbn(dto.isbn());
        return book;
    }
}
