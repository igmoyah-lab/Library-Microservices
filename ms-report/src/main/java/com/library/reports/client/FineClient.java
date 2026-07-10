package com.library.reports.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.library.reports.dto.ApiResponse;
import com.library.reports.exception.ExternalServiceException;

@Component
public class FineClient {

    private final RestClient restClient;

    public FineClient(
            @Value("${fine.service.base-url}") String fineBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(fineBaseUrl)
                .build();
    }

    public long getTotalFines() {
        try {
            ApiResponse<Long> response = restClient.get()
                    .uri("/api/fines/count")
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<Long>>() {
                    });

            if (response == null || response.data() == null) {
                throw new ExternalServiceException(
                        "ms-fines entregó una respuesta vacía"
                );
            }

            return response.data();

        } catch (Exception ex) {
            throw new ExternalServiceException(
                    "No fue posible obtener los datos de ms-fines"
            );
        }
    }
}
