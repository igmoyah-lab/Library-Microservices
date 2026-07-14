package com.library.returns.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.library.returns.client.dto.LoanClientResponse;
import com.library.returns.dto.ApiResponse;
import com.library.returns.exception.BusinessRuleException;
import com.library.returns.exception.ExternalServiceException;
import com.library.returns.exception.ResourceNotFoundException;

@Component
public class LoanClient {

    private final RestClient restClient;

    public LoanClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.loan.base-url:http://localhost:5004}")
            String baseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Consulta un préstamo en el microservicio ms-loan.
     *
     * @param loanId identificador del préstamo
     * @return préstamo encontrado
     */
public LoanClientResponse getLoanById(UUID loanId) {
    try {
        ApiResponse<LoanClientResponse> response =
                restClient.get()
                        .uri("/api/loans/{loanId}", loanId)
                        .retrieve()
                        .body(
                                new ParameterizedTypeReference<
                                        ApiResponse<LoanClientResponse>
                                >() {
                                }
                        );

        return extractLoan(response);

    } catch (HttpClientErrorException.NotFound exception) {
        throw new ResourceNotFoundException(
                "Préstamo no encontrado con id: " + loanId
        );

    } catch (RestClientException exception) {
        throw new ExternalServiceException(
                "No fue posible comunicarse con ms-loan",
                exception
        );
    }
}

/**
 * Marca un préstamo como devuelto en ms-loan.
 *
 * @param loanId identificador del préstamo
 * @return préstamo actualizado
 */
public LoanClientResponse markLoanAsReturned(UUID loanId) {
    try {
        ApiResponse<LoanClientResponse> response =
                restClient.patch()
                        .uri(
                                "/api/loans/{loanId}/return",
                                loanId
                        )
                        .retrieve()
                        .body(
                                new ParameterizedTypeReference<
                                        ApiResponse<LoanClientResponse>
                                >() {
                                }
                        );

        return extractLoan(response);

    } catch (HttpClientErrorException.NotFound exception) {
        throw new ResourceNotFoundException(
                "Préstamo no encontrado con id: " + loanId
        );

    } catch (HttpClientErrorException.Conflict exception) {
        throw new BusinessRuleException(
                "El préstamo ya se encuentra devuelto"
        );

    } catch (RestClientException exception) {
        throw new ExternalServiceException(
                "No fue posible actualizar el préstamo en ms-loan",
                exception
        );
    }
}

/**
 * Extrae el préstamo desde la respuesta estándar.
 *
 * @param response respuesta recibida desde ms-loan
 * @return datos del préstamo
 */
private LoanClientResponse extractLoan(
        ApiResponse<LoanClientResponse> response
) {
    if (response == null || response.data() == null) {
        throw new ExternalServiceException(
                "ms-loan devolvió una respuesta vacía"
        );
    }

    return response.data();
}
}