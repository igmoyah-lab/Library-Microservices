package com.library.reservations.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.library.reservations.client.dto.BookClientResponse;
import com.library.reservations.dto.ApiResponse;
import com.library.reservations.exception.ExternalServiceException;
import com.library.reservations.exception.ResourceNotFoundException;

@Component
public class BookClient {

    private final RestClient restClient;

    public BookClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.book.base-url:http://localhost:5001}")
            String baseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Consulta un libro en ms-book.
     *
     * @param bookId identificador del libro
     * @return libro encontrado
     */
    public BookClientResponse getBookById(UUID bookId) {
        try {
            ApiResponse<BookClientResponse> response =
                    restClient.get()
                            .uri("/api/books/{id}", bookId)
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                            ApiResponse<BookClientResponse>
                                    >() {
                                    }
                            );

            if (response == null || response.data() == null) {
                throw new ExternalServiceException(
                        "ms-book devolvió una respuesta vacía"
                );
            }

            return response.data();

        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException(
                    "Libro no encontrado con id: " + bookId
            );

        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "No fue posible comunicarse con ms-book",
                    exception
            );
        }
    }
}
