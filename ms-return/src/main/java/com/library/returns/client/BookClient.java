package com.library.returns.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.library.returns.client.dto.BookClientResponse;
import com.library.returns.dto.ApiResponse;
import com.library.returns.exception.ExternalServiceException;
import com.library.returns.exception.ResourceNotFoundException;

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
 * Cambia la disponibilidad de un libro en ms-book.
 *
 * @param bookId identificador del libro
 * @param available nuevo estado de disponibilidad
 * @return libro actualizado
 */
public BookClientResponse updateAvailability(
        UUID bookId,
        boolean available
) {
    try {
        ApiResponse<BookClientResponse> response =
                restClient.patch()
                        .uri(uriBuilder ->
                                uriBuilder
                                        .path(
                                                "/api/books/{id}/availability"
                                        )
                                        .queryParam(
                                                "available",
                                                available
                                        )
                                        .build(bookId)
                        )
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
                "No fue posible actualizar el libro en ms-book",
                exception
        );
    }
}
}