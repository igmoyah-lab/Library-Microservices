package com.library.notifications.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.notifications.dto.ApiResponse;
import com.library.notifications.dto.NotificationRequest;
import com.library.notifications.dto.NotificationResponse;
import com.library.notifications.entity.Notification;
import com.library.notifications.exception.BusinessRuleException;
import com.library.notifications.exception.ResourceNotFoundException;
import com.library.notifications.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    private NotificationServiceImpl createService() {
        return new NotificationServiceImpl(
                notificationRepository
        );
    }

    @Test
    void createNotification_shouldCreateUnreadNotification() {
        NotificationServiceImpl notificationService =
                createService();

        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        NotificationRequest request =
                new NotificationRequest(
                        userId,
                        "Tu reserva fue creada"
                );

        when(
                notificationRepository.save(
                        any(Notification.class)
                )
        ).thenAnswer(invocation -> {
            Notification notification =
                    invocation.getArgument(0);

            notification.setId(notificationId);

            return notification;
        });

        ApiResponse<NotificationResponse> result =
                notificationService.createNotification(
                        request
                );

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(
                notificationId,
                result.data().id()
        );
        assertEquals(
                userId,
                result.data().userId()
        );
        assertEquals(
                "Tu reserva fue creada",
                result.data().message()
        );
        assertFalse(result.data().read());
        assertNotNull(result.data().createdAt());
        assertEquals(
                "Notificación creada con éxito",
                result.message()
        );

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(
                        Notification.class
                );

        verify(notificationRepository)
                .save(captor.capture());

        Notification savedNotification =
                captor.getValue();

        assertEquals(
                userId,
                savedNotification.getUserId()
        );
        assertEquals(
                "Tu reserva fue creada",
                savedNotification.getMessage()
        );
        assertFalse(savedNotification.isRead());
        assertNotNull(
                savedNotification.getCreatedAt()
        );
    }

    @Test
    void getNotificationById_shouldReturnNotification() {
        NotificationServiceImpl notificationService =
                createService();

        UUID notificationId = UUID.randomUUID();

        Notification notification =
                notificationEntity(
                        notificationId,
                        UUID.randomUUID(),
                        "Notificación de prueba",
                        false
                );

        when(
                notificationRepository.findById(
                        notificationId
                )
        ).thenReturn(
                Optional.of(notification)
        );

        ApiResponse<NotificationResponse> result =
                notificationService
                        .getNotificationById(
                                notificationId
                        );

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(
                notificationId,
                result.data().id()
        );
        assertEquals(
                "Notificación de prueba",
                result.data().message()
        );
        assertFalse(result.data().read());
        assertEquals(
                "Notificación obtenida con éxito",
                result.message()
        );
    }

    @Test
    void getNotificationById_shouldThrowWhenMissing() {
        NotificationServiceImpl notificationService =
                createService();

        UUID notificationId = UUID.randomUUID();

        when(
                notificationRepository.findById(
                        notificationId
                )
        ).thenReturn(
                Optional.empty()
        );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> notificationService
                                .getNotificationById(
                                        notificationId
                                )
                );

        assertEquals(
                "No se encontró la notificación con id: "
                        + notificationId,
                exception.getMessage()
        );
    }

    @Test
    void getNotificationsByUserId_shouldReturnNotifications() {
        NotificationServiceImpl notificationService =
                createService();

        UUID userId = UUID.randomUUID();

        Notification firstNotification =
                notificationEntity(
                        UUID.randomUUID(),
                        userId,
                        "Primera notificación",
                        false
                );

        Notification secondNotification =
                notificationEntity(
                        UUID.randomUUID(),
                        userId,
                        "Segunda notificación",
                        true
                );

        when(
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                userId
                        )
        ).thenReturn(
                List.of(
                        firstNotification,
                        secondNotification
                )
        );

        ApiResponse<List<NotificationResponse>> result =
                notificationService
                        .getNotificationsByUserId(
                                userId
                        );

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals(2, result.data().size());
        assertEquals(
                userId,
                result.data().get(0).userId()
        );
        assertEquals(
                "Primera notificación",
                result.data().get(0).message()
        );
        assertFalse(
                result.data().get(0).read()
        );
        assertTrue(
                result.data().get(1).read()
        );
        assertEquals(
                "Notificaciones obtenidas con éxito",
                result.message()
        );
    }

    @Test
    void getNotificationsByUserId_shouldThrowWhenEmpty() {
        NotificationServiceImpl notificationService =
                createService();

        UUID userId = UUID.randomUUID();

        when(
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                userId
                        )
        ).thenReturn(
                List.of()
        );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> notificationService
                                .getNotificationsByUserId(
                                        userId
                                )
                );

        assertEquals(
                "No se encontraron notificaciones para el usuario: "
                        + userId,
                exception.getMessage()
        );
    }

    @Test
    void markAsRead_shouldMarkNotificationAsRead() {
        NotificationServiceImpl notificationService =
                createService();

        UUID notificationId = UUID.randomUUID();

        Notification notification =
                notificationEntity(
                        notificationId,
                        UUID.randomUUID(),
                        "Notificación pendiente",
                        false
                );

        when(
                notificationRepository.findById(
                        notificationId
                )
        ).thenReturn(
                Optional.of(notification)
        );

        when(
                notificationRepository.save(
                        any(Notification.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        ApiResponse<NotificationResponse> result =
                notificationService.markAsRead(
                        notificationId
                );

        assertTrue(result.success());
        assertNotNull(result.data());
        assertTrue(result.data().read());
        assertTrue(notification.isRead());
        assertEquals(
                "Notificación marcada como leída",
                result.message()
        );

        verify(notificationRepository)
                .save(notification);
    }

    @Test
    void markAsRead_shouldRejectAlreadyReadNotification() {
        NotificationServiceImpl notificationService =
                createService();

        UUID notificationId = UUID.randomUUID();

        Notification notification =
                notificationEntity(
                        notificationId,
                        UUID.randomUUID(),
                        "Notificación leída",
                        true
                );

        when(
                notificationRepository.findById(
                        notificationId
                )
        ).thenReturn(
                Optional.of(notification)
        );

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> notificationService
                                .markAsRead(
                                        notificationId
                                )
                );

        assertEquals(
                "La notificación ya se encuentra leída",
                exception.getMessage()
        );

        verify(
                notificationRepository,
                never()
        ).save(
                any(Notification.class)
        );
    }

    @Test
    void markAsRead_shouldThrowWhenNotificationDoesNotExist() {
        NotificationServiceImpl notificationService =
                createService();

        UUID notificationId = UUID.randomUUID();

        when(
                notificationRepository.findById(
                        notificationId
                )
        ).thenReturn(
                Optional.empty()
        );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> notificationService
                                .markAsRead(
                                        notificationId
                                )
                );

        assertEquals(
                "No se encontró la notificación con id: "
                        + notificationId,
                exception.getMessage()
        );

        verify(
                notificationRepository,
                never()
        ).save(
                any(Notification.class)
        );
    }

    @Test
    void countUnreadNotifications_shouldReturnUnreadCount() {
        NotificationServiceImpl notificationService =
                createService();

        UUID userId = UUID.randomUUID();

        when(
                notificationRepository
                        .countByUserIdAndReadFalse(
                                userId
                        )
        ).thenReturn(4L);

        long result =
                notificationService
                        .countUnreadNotifications(
                                userId
                        );

        assertEquals(4L, result);

        verify(notificationRepository)
                .countByUserIdAndReadFalse(
                        userId
                );
    }

    private Notification notificationEntity(
            UUID notificationId,
            UUID userId,
            String message,
            boolean read
    ) {
        Notification notification =
                new Notification();

        notification.setId(notificationId);
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setCreatedAt(
                LocalDateTime.now()
        );
        notification.setRead(read);

        return notification;
    }
}
