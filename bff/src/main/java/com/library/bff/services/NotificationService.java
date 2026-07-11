package com.library.bff.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.bff.client.NotificationClient;
import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.NotificationResponse;

@Service
public class NotificationService {

    private final NotificationClient notificationClient;

    public NotificationService(
            NotificationClient notificationClient
    ) {
        this.notificationClient = notificationClient;
    }

    /**
     * Obtiene una notificación mediante su ID.
     *
     * @param notificationId identificador de la notificación
     * @return notificación encontrada
     */
    public ApiResponse<NotificationResponse>
            getNotificationById(UUID notificationId) {

        return notificationClient.getNotificationById(
                notificationId
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

        return notificationClient
                .getNotificationsByUserId(userId);
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
        return notificationClient.markAsRead(
                notificationId
        );
    }

    /**
     * Cuenta las notificaciones no leídas.
     *
     * @param userId identificador del usuario
     * @return cantidad de notificaciones no leídas
     */
    public ApiResponse<Long> countUnreadNotifications(
            UUID userId
    ) {
        return notificationClient
                .countUnreadNotifications(userId);
    }
}
