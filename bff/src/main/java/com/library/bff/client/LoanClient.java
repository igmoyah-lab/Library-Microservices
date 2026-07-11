package com.library.bff.client;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.LoanRequest;
import com.library.bff.dto.LoanResponse;

@Component
public class LoanClient {

    private final RestClient restClient;

    public LoanClient(
            @Value("${loan.service.base-url}")
            String loanBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(loanBaseUrl)
                .build();
    }

    /**
     * Solicita la creación de un préstamo en ms-loan.
     *
     * @param request datos del préstamo
     * @return préstamo creado
     */
    public ApiResponse<LoanResponse> createLoan(
            LoanRequest request
    ) {
        return restClient.post()
                .uri("/api/loans")
                .body(request)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<LoanResponse>
                        >() {
                        }
                );
    }

    /**
     * Consulta un préstamo mediante su identificador.
     *
     * @param loanId identificador del préstamo
     * @return préstamo encontrado
     */
    public ApiResponse<LoanResponse> getLoanById(
            UUID loanId
    ) {
        return restClient.get()
                .uri("/api/loans/{loanId}", loanId)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<LoanResponse>
                        >() {
                        }
                );
    }

    /**
     * Consulta todos los préstamos de un usuario.
     *
     * @param userId identificador del usuario
     * @return préstamos encontrados
     */
    public ApiResponse<List<LoanResponse>> getLoansByUserId(
            UUID userId
    ) {
        return restClient.get()
                .uri("/api/loans/user/{userId}", userId)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<List<LoanResponse>>
                        >() {
                        }
                );
    }

    /**
     * Obtiene el total de préstamos registrados.
     *
     * @return total de préstamos
     */
    public ApiResponse<Long> countLoans() {
        return restClient.get()
                .uri("/api/loans/count")
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<Long>
                        >() {
                        }
                );
    }
}
