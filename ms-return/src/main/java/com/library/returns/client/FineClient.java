package com.library.returns.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.library.returns.client.dto.FineClientRequest;
import com.library.returns.client.dto.FineClientResponse;
import com.library.returns.dto.ApiResponse;
import com.library.returns.exception.BusinessRuleException;
import com.library.returns.exception.ExternalServiceException;

@Component
public class FineClient {

    private final RestClient restClient;

    public FineClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.fine.base-url:http://localhost:5007}")
            String baseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Crea una multa en el microservicio ms-fine.
     *
     * @param request datos necesarios para crear la multa
     * @return multa creada
     */
    public FineClientResponse createFine(
            FineClientRequest request
    ) {
        try {
            ApiResponse<FineClientResponse> response =
                    restClient.post()
                            .uri("/api/fines")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(request)
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                            ApiResponse<FineClientResponse>
                                    >() {
                                    }
                            );

            if (response == null || response.data() == null) {
                throw new ExternalServiceException(
                        "ms-fine devolvió una respuesta vacía"
                );
            }

            return response.data();

        } catch (HttpClientErrorException.Conflict exception) {
            throw new BusinessRuleException(
                    "El préstamo ya tiene una multa registrada"
            );

        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "No fue posible crear la multa en ms-fine",
                    exception
            );
        }
    }
}