package com.library.notifications.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.library.notifications.dto.ApiResponse;
import com.library.notifications.dto.NotificationRequest;
import com.library.notifications.dto.NotificationResponse;
import com.library.notifications.entity.Notification;
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

    @Override
    public ApiResponse<NotificationResponse> createNotification(
            NotificationRequest notificationRequest
    ) {

        Notification notification = new Notification();

        notification.setUserId(notificationRequest.userId());
        notification.setMessage(notificationRequest.message());
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification =
                notificationRepository.save(notification);

        return new ApiResponse<>(
                true,
                mapToResponse(savedNotification),
                "Notificación creada con éxito"
        );
    }

    @Override
    public ApiResponse<List<NotificationResponse>>
            getNotificationsByUserId(UUID userId) {

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(userId);

        if (notifications.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron notificaciones para el usuario: "
                            + userId
            );
        }

        List<NotificationResponse> responses = notifications.stream()
                .map(this::mapToResponse)
                .toList();

        return new ApiResponse<>(
                true,
                responses,
                "Notificaciones obtenidas con éxito"
        );
    }

    private NotificationResponse mapToResponse(
            Notification notification
    ) {

        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getMessage(),
                notification.getCreatedAt()
        );
    }
}