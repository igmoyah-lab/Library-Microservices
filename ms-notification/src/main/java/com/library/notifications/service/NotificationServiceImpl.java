package com.library.notifications.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.notifications.dto.ApiResponse;
import com.library.notifications.dto.NotificationRequest;
import com.library.notifications.dto.NotificationResponse;
import com.library.notifications.entity.Notification;
import com.library.notifications.exception.BusinessRuleException;
import com.library.notifications.exception.ResourceNotFoundException;
import com.library.notifications.repository.NotificationRepository;

@Service
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository
    ) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Crea una notificación nueva para un usuario.
     * Se registra inicialmente como no leída.
     *
     * @param notificationRequest datos de la notificación
     * @return respuesta con la notificación creada
     */
    @Override
    public ApiResponse<NotificationResponse> createNotification(
            NotificationRequest notificationRequest
    ) {
        Notification notification = new Notification();

        notification.setUserId(
                notificationRequest.userId()
        );

        notification.setMessage(
                notificationRequest.message()
        );

        notification.setCreatedAt(
                LocalDateTime.now()
        );

        notification.setRead(false);

        Notification savedNotification =
                notificationRepository.save(notification);

        return new ApiResponse<>(
                true,
                mapToResponse(savedNotification),
                "Notificación creada con éxito"
        );
    }

    /**
     * Obtiene una notificación mediante su identificador.
     *
     * @param id identificador de la notificación
     * @return respuesta con la notificación encontrada
     * @throws ResourceNotFoundException si no existe
     */
    @Override
    public ApiResponse<NotificationResponse>
            getNotificationById(UUID id) {
        Notification notification =
                findNotificationById(id);

        return new ApiResponse<>(
                true,
                mapToResponse(notification),
                "Notificación obtenida con éxito"
        );
    }

    /**
     * Obtiene las notificaciones de un usuario,
     * ordenadas desde la más reciente.
     *
     * @param userId identificador del usuario
     * @return respuesta con las notificaciones
     * @throws ResourceNotFoundException si no existen
     */
    @Override
    public ApiResponse<List<NotificationResponse>>
            getNotificationsByUserId(UUID userId) {
        List<Notification> notifications =
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                userId
                        );

        if (notifications.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron notificaciones para el usuario: "
                            + userId
            );
        }

        List<NotificationResponse> responses =
                notifications.stream()
                        .map(this::mapToResponse)
                        .toList();

        return new ApiResponse<>(
                true,
                responses,
                "Notificaciones obtenidas con éxito"
        );
    }

    /**
     * Marca una notificación pendiente como leída.
     *
     * @param id identificador de la notificación
     * @return respuesta con la notificación actualizada
     * @throws ResourceNotFoundException si no existe
     * @throws BusinessRuleException si ya estaba leída
     */
    @Override
    public ApiResponse<NotificationResponse> markAsRead(
            UUID id
    ) {
        Notification notification =
                findNotificationById(id);

        if (notification.isRead()) {
            throw new BusinessRuleException(
                    "La notificación ya se encuentra leída"
            );
        }

        notification.setRead(true);

        Notification updatedNotification =
                notificationRepository.save(notification);

        return new ApiResponse<>(
                true,
                mapToResponse(updatedNotification),
                "Notificación marcada como leída"
        );
    }

    /**
     * Cuenta las notificaciones no leídas
     * pertenecientes a un usuario.
     *
     * @param userId identificador del usuario
     * @return cantidad de notificaciones no leídas
     */
    @Override
    public long countUnreadNotifications(UUID userId) {
        return notificationRepository
                .countByUserIdAndReadFalse(userId);
    }

    /**
     * Busca internamente una notificación por ID.
     *
     * @param id identificador de la notificación
     * @return notificación encontrada
     * @throws ResourceNotFoundException si no existe
     */
    private Notification findNotificationById(UUID id) {
        return notificationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró la notificación con id: "
                                        + id
                        )
                );
    }

    /**
     * Convierte una entidad Notification
     * en NotificationResponse.
     *
     * @param notification entidad de notificación
     * @return DTO de respuesta
     */
    private NotificationResponse mapToResponse(
            Notification notification
    ) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.isRead()
        );
    }
}