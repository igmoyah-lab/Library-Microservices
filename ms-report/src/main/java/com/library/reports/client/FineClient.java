package com.library.reports.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.library.reports.dto.ApiResponse;
import com.library.reports.exception.ExternalServiceException;

@Component
public class FineClient {

    private final RestClient restClient;

    public FineClient(
            RestClient.Builder restClientBuilder,
            @Value(
                "${services.fine.base-url:http://localhost:5007}"
            )
            String baseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Obtiene la cantidad total de multas.
     *
     * @return cantidad total de multas
     */
    public long getTotalFines() {
        return getCount(
                "/api/fines/count",
                "total de multas"
        );
    }

    /**
     * Obtiene la cantidad de multas pendientes.
     *
     * @return cantidad de multas pendientes
     */
    public long getPendingFines() {
        return getCount(
                "/api/fines/count/pending",
                "multas pendientes"
        );
    }

    /**
     * Obtiene la cantidad de multas pagadas.
     *
     * @return cantidad de multas pagadas
     */
    public long getPaidFines() {
        return getCount(
                "/api/fines/count/paid",
                "multas pagadas"
        );
    }

    /**
     * Ejecuta internamente una consulta de conteo
     * en el microservicio de multas.
     *
     * @param uri endpoint consultado
     * @param description descripción del dato
     * @return cantidad obtenida
     */
    private long getCount(
            String uri,
            String description
    ) {
        try {
            ApiResponse<Long> response =
                    restClient.get()
                            .uri(uri)
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                            ApiResponse<Long>
                                    >() {
                                    }
                            );

            if (response == null || response.data() == null) {
                throw new ExternalServiceException(
                        "ms-fine devolvió una respuesta vacía"
                );
            }

            return response.data();

        } catch (ExternalServiceException exception) {
            throw exception;

        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "No fue posible obtener "
                            + description
                            + " desde ms-fine",
                    exception
            );
        }
    }
}