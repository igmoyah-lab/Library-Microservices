package com.library.bff.client;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.FineResponse;

@Component
public class FineClient {

    private final RestClient restClient;

    public FineClient(
            @Value("${fine.service.base-url}")
            String fineBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(fineBaseUrl)
                .build();
    }

    /**
     * Obtiene una multa mediante su identificador.
     *
     * @param fineId identificador de la multa
     * @return multa encontrada
     */
    public ApiResponse<FineResponse> getFineById(
            UUID fineId
    ) {
        return restClient.get()
                .uri("/api/fines/{id}", fineId)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<FineResponse>
                        >() {
                        }
                );
    }

    /**
     * Obtiene todas las multas de un usuario.
     *
     * @param userId identificador del usuario
     * @return multas encontradas
     */
    public ApiResponse<List<FineResponse>> getFinesByUserId(
            UUID userId
    ) {
        return restClient.get()
                .uri("/api/fines/user/{userId}", userId)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<List<FineResponse>>
                        >() {
                        }
                );
    }

    /**
     * Marca una multa pendiente como pagada.
     *
     * @param fineId identificador de la multa
     * @return multa actualizada
     */
    public ApiResponse<FineResponse> payFine(
            UUID fineId
    ) {
        return restClient.patch()
                .uri("/api/fines/{id}/pay", fineId)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<FineResponse>
                        >() {
                        }
                );
    }

    /**
     * Obtiene el total de multas registradas.
     *
     * @return total de multas
     */
    public ApiResponse<Long> countFines() {
        return getCount("/api/fines/count");
    }

    /**
     * Obtiene el total de multas pendientes.
     *
     * @return total de multas pendientes
     */
    public ApiResponse<Long> countPendingFines() {
        return getCount("/api/fines/count/pending");
    }

    /**
     * Obtiene el total de multas pagadas.
     *
     * @return total de multas pagadas
     */
    public ApiResponse<Long> countPaidFines() {
        return getCount("/api/fines/count/paid");
    }

    /**
     * Ejecuta una consulta de conteo en ms-fine.
     *
     * @param uri endpoint de conteo
     * @return respuesta con la cantidad obtenida
     */
    private ApiResponse<Long> getCount(String uri) {
        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<Long>
                        >() {
                        }
                );
    }
}
