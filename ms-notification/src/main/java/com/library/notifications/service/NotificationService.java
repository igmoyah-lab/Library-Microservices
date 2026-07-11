package com.library.notifications.service;

import java.util.List;
import java.util.UUID;

import com.library.notifications.dto.ApiResponse;
import com.library.notifications.dto.NotificationRequest;
import com.library.notifications.dto.NotificationResponse;

public interface NotificationService {

    /**
     * Crea una notificación nueva para un usuario.
     *
     * @param notificationRequest datos de la notificación
     * @return respuesta con la notificación creada
     */
    ApiResponse<NotificationResponse> createNotification(
            NotificationRequest notificationRequest
    );

    /**
     * Obtiene una notificación mediante su identificador.
     *
     * @param id identificador de la notificación
     * @return respuesta con la notificación encontrada
     */
    ApiResponse<NotificationResponse> getNotificationById(
            UUID id
    );

    /**
     * Obtiene todas las notificaciones de un usuario.
     *
     * @param userId identificador del usuario
     * @return respuesta con las notificaciones
     */
    ApiResponse<List<NotificationResponse>>
            getNotificationsByUserId(UUID userId);

    /**
     * Marca una notificación como leída.
     *
     * @param id identificador de la notificación
     * @return respuesta con la notificación actualizada
     */
    ApiResponse<NotificationResponse> markAsRead(UUID id);

    /**
     * Cuenta las notificaciones no leídas de un usuario.
     *
     * @param userId identificador del usuario
     * @return cantidad de notificaciones no leídas
     */
    long countUnreadNotifications(UUID userId);
}
