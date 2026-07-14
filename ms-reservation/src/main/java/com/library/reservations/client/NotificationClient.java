package com.library.reservations.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.library.reservations.client.dto.NotificationClientRequest;
import com.library.reservations.client.dto.NotificationClientResponse;
import com.library.reservations.dto.ApiResponse;
import com.library.reservations.exception.ExternalServiceException;

@Component
public class NotificationClient {

    private final RestClient restClient;

    public NotificationClient(
            RestClient.Builder restClientBuilder,
            @Value(
                "${services.notification.base-url:http://localhost:5008}"
            )
            String baseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Crea una notificación en ms-notification.
     *
     * @param request información de la notificación
     * @return notificación creada
     */
    public NotificationClientResponse createNotification(
            NotificationClientRequest request
    ) {
        try {
            ApiResponse<NotificationClientResponse> response =
                    restClient.post()
                            .uri("/api/notifications")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(request)
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                        ApiResponse<NotificationClientResponse>
                                    >() {
                                    }
                            );

            if (response == null || response.data() == null) {
                throw new ExternalServiceException(
                        "ms-notification devolvió una respuesta vacía"
                );
            }

            return response.data();

        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "No fue posible crear la notificación",
                    exception
            );
        }
    }
}
