package com.library.bff.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.ReturnRequest;
import com.library.bff.dto.ReturnResponse;

@Component
public class ReturnClient {

    private final RestClient restClient;

    public ReturnClient(
            @Value("${return.service.base-url}")
            String returnBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(returnBaseUrl)
                .build();
    }

    /**
     * Registra una devolución mediante ms-return.
     *
     * @param request identificador del préstamo
     * @return devolución registrada
     */
    public ApiResponse<ReturnResponse> createReturn(
            ReturnRequest request
    ) {
        return restClient.post()
                .uri("/api/returns")
                .body(request)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<ReturnResponse>
                        >() {
                        }
                );
    }

    /**
     * Busca una devolución mediante el ID del préstamo.
     *
     * @param loanId identificador del préstamo
     * @return devolución encontrada
     */
    public ApiResponse<ReturnResponse> getReturnByLoanId(
            UUID loanId
    ) {
        return restClient.get()
                .uri("/api/returns/loan/{loanId}", loanId)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<ReturnResponse>
                        >() {
                        }
                );
    }
}