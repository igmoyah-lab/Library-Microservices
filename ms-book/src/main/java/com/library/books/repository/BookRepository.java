package com.library.books.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.books.entity.Book;

public interface BookRepository extends JpaRepository<Book, UUID> {

    List<Book> findByTitleIgnoreCase(String title);
}


