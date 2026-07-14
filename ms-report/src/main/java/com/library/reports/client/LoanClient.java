package com.library.reports.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.library.reports.dto.ApiResponse;
import com.library.reports.exception.ExternalServiceException;

@Component
public class LoanClient {

    private final RestClient restClient;

    public LoanClient(
            RestClient.Builder restClientBuilder,
            @Value(
                "${services.loan.base-url:http://localhost:5004}"
            )
            String baseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Obtiene la cantidad total de préstamos registrados.
     *
     * @return cantidad total de préstamos
     */
    public long getTotalLoans() {
        try {
            ApiResponse<Long> response =
                    restClient.get()
                            .uri("/api/loans/count")
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                            ApiResponse<Long>
                                    >() {
                                    }
                            );

            if (response == null || response.data() == null) {
                throw new ExternalServiceException(
                        "ms-loan devolvió una respuesta vacía"
                );
            }

            return response.data();

        } catch (ExternalServiceException exception) {
            throw exception;

        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "No fue posible obtener el total de préstamos desde ms-loan",
                    exception
            );
        }
    }
}