package com.library.bff.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.bff.dto.ApiResponse;
import com.library.bff.dto.NotificationResponse;
import com.library.bff.services.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }

    /**
     * Obtiene una notificación mediante su ID.
     *
     * @param notificationId identificador de la notificación
     * @return notificación encontrada
     */
    @GetMapping("/{notificationId}")
    public ApiResponse<NotificationResponse>
            getNotificationById(
                    @PathVariable UUID notificationId
            ) {
        return notificationService.getNotificationById(
                notificationId
        );
    }

    /**
     * Obtiene las notificaciones de un usuario.
     *
     * @param userId identificador del usuario
     * @return notificaciones encontradas
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<NotificationResponse>>
            getNotificationsByUserId(
                    @PathVariable UUID userId
            ) {
        return notificationService
                .getNotificationsByUserId(userId);
    }

    /**
     * Marca una notificación como leída.
     *
     * @param notificationId identificador de la notificación
     * @return notificación actualizada
     */
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<NotificationResponse> markAsRead(
            @PathVariable UUID notificationId
    ) {
        return notificationService.markAsRead(
                notificationId
        );
    }

    /**
     * Cuenta las notificaciones no leídas de un usuario.
     *
     * @param userId identificador del usuario
     * @return cantidad de notificaciones no leídas
     */
    @GetMapping("/user/{userId}/unread/count")
    public ApiResponse<Long> countUnreadNotifications(
            @PathVariable UUID userId
    ) {
        return notificationService
                .countUnreadNotifications(userId);
    }
}
