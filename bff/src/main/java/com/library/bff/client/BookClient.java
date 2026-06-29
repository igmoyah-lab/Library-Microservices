package com.library.bff.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.BookRequest;
import com.library.bff.dto.BookResponse;
import com.library.bff.dto.BookSearchRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BookClient {

    private final RestClient restClient;

    public BookClient(@Value("${book.service.base-url}") String bookBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(bookBaseUrl)
                .build();
    }

    public ApiResponse<List<BookResponse>> getAllBooks() {
        return restClient.get()
                .uri("/api/books")
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<List<BookResponse>>>() {});
    }

    public ApiResponse<BookResponse> getBookById(UUID id) {
        return restClient.get()
                .uri("/api/books/{id}", id)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<BookResponse>>() {});
    }

    public ApiResponse<List<BookResponse>> searchBooks(BookSearchRequest request) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/books/search")
                        .queryParamIfPresent("title", Optional.ofNullable(request.title()))
                        .queryParamIfPresent("author", Optional.ofNullable(request.author()))
                        .queryParamIfPresent("isbn", Optional.ofNullable(request.isbn()))
                        .build()
                )
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<List<BookResponse>>>() {});
    }

    public ApiResponse<BookResponse> createBook(BookRequest request) {
        return restClient.post()
                .uri("/api/books")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<BookResponse>>() {});
    }

    public ApiResponse<BookResponse> updateBook(UUID id, BookRequest request) {
        return restClient.put()
                .uri("/api/books/{id}", id)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<BookResponse>>() {});
    }

    public ApiResponse<Void> deleteBook(UUID id) {
        return restClient.delete()
                .uri("/api/books/{id}", id)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<Void>>() {});
    }
}