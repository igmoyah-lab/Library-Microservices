package com.library.books.repository;

import com.library.books.entity.Book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void findByTitleIgnoreCase_shouldFindBookIgnoringCase() {
        Book book = new Book();
        book.setTitle("El Principito");
        book.setAuthor("Antoine de Saint-Exupéry");
        book.setCategory("Literatura");
        book.setIsbn("123456789");

        bookRepository.saveAndFlush(book);

        var result = bookRepository.findByTitleIgnoreCase("eL pRiNcIpItO");

        assertFalse(result.isEmpty());
        assertEquals("El Principito", result.get(0).getTitle());
    }
}