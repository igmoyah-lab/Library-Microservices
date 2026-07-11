package com.library.bff.client;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.NotificationResponse;

@Component
public class NotificationClient {

    private final RestClient restClient;

    public NotificationClient(
            @Value("${notification.service.base-url}")
            String notificationBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(notificationBaseUrl)
                .build();
    }

    /**
     * Obtiene una notificación mediante su identificador.
     *
     * @param notificationId identificador de la notificación
     * @return notificación encontrada
     */
    public ApiResponse<NotificationResponse>
            getNotificationById(UUID notificationId) {

        return restClient.get()
                .uri(
                        "/api/notifications/{id}",
                        notificationId
                )
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<NotificationResponse>
                        >() {
                        }
                );
    }

    /**
     * Obtiene las notificaciones de un usuario.
     *
     * @param userId identificador del usuario
     * @return notificaciones encontradas
     */
    public ApiResponse<List<NotificationResponse>>
            getNotificationsByUserId(UUID userId) {

        return restClient.get()
                .uri(
                        "/api/notifications/user/{userId}",
                        userId
                )
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<List<NotificationResponse>>
                        >() {
                        }
                );
    }

    /**
     * Marca una notificación como leída.
     *
     * @param notificationId identificador de la notificación
     * @return notificación actualizada
     */
    public ApiResponse<NotificationResponse> markAsRead(
            UUID notificationId
    ) {
        return restClient.patch()
                .uri(
                        "/api/notifications/{id}/read",
                        notificationId
                )
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<NotificationResponse>
                        >() {
                        }
                );
    }

    /**
     * Cuenta las notificaciones no leídas de un usuario.
     *
     * @param userId identificador del usuario
     * @return cantidad de notificaciones no leídas
     */
    public ApiResponse<Long> countUnreadNotifications(
            UUID userId
    ) {
        return restClient.get()
                .uri(
                        "/api/notifications/user/{userId}/unread/count",
                        userId
                )
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                ApiResponse<Long>
                        >() {
                        }
                );
    }
}
