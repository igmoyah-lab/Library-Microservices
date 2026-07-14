package com.library.notifications.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.library.notifications.dto.ApiResponse;
import com.library.notifications.dto.NotificationRequest;
import com.library.notifications.dto.NotificationResponse;
import com.library.notifications.service.NotificationService;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationRequest notificationRequest;

    @Mock
    private NotificationResponse notificationResponse;

    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        notificationController =
                new NotificationController(notificationService);
    }

    @Test
    void createNotification_shouldReturnCreatedStatus() {
        ApiResponse<NotificationResponse> serviceResponse =
                new ApiResponse<>(
                        true,
                        notificationResponse,
                        "Notificación creada con éxito"
                );

        when(notificationService.createNotification(notificationRequest))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<NotificationResponse>> response =
                notificationController.createNotification(
                        notificationRequest
                );

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(serviceResponse, response.getBody());

        verify(notificationService)
                .createNotification(notificationRequest);
    }

    @Test
    void getNotificationById_shouldReturnOkStatus() {
        UUID notificationId = UUID.randomUUID();

        ApiResponse<NotificationResponse> serviceResponse =
                new ApiResponse<>(
                        true,
                        notificationResponse,
                        "Notificación encontrada con éxito"
                );

        when(notificationService.getNotificationById(notificationId))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<NotificationResponse>> response =
                notificationController.getNotificationById(
                        notificationId
                );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(serviceResponse, response.getBody());

        verify(notificationService)
                .getNotificationById(notificationId);
    }

    @Test
    void getNotificationsByUserId_shouldReturnOkStatus() {
        UUID userId = UUID.randomUUID();

        List<NotificationResponse> notifications =
                List.of(notificationResponse);

        ApiResponse<List<NotificationResponse>> serviceResponse =
                new ApiResponse<>(
                        true,
                        notifications,
                        "Notificaciones obtenidas con éxito"
                );

        when(notificationService.getNotificationsByUserId(userId))
                .thenReturn(serviceResponse);

        ResponseEntity<
                ApiResponse<List<NotificationResponse>>
        > response =
                notificationController.getNotificationsByUserId(
                        userId
                );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(serviceResponse, response.getBody());

        verify(notificationService)
                .getNotificationsByUserId(userId);
    }

    @Test
    void markAsRead_shouldReturnOkStatus() {
        UUID notificationId = UUID.randomUUID();

        ApiResponse<NotificationResponse> serviceResponse =
                new ApiResponse<>(
                        true,
                        notificationResponse,
                        "Notificación marcada como leída"
                );

        when(notificationService.markAsRead(notificationId))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<NotificationResponse>> response =
                notificationController.markAsRead(notificationId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(serviceResponse, response.getBody());

        verify(notificationService)
                .markAsRead(notificationId);
    }

    @Test
    void countUnreadNotifications_shouldReturnUnreadCount() {
        UUID userId = UUID.randomUUID();
        long unreadCount = 5L;

        when(notificationService.countUnreadNotifications(userId))
                .thenReturn(unreadCount);

        ResponseEntity<ApiResponse<Long>> response =
                notificationController.countUnreadNotifications(
                        userId
                );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(true, response.getBody().success());
        assertEquals(unreadCount, response.getBody().data());
        assertEquals(
                "Cantidad de notificaciones no leídas obtenida con éxito",
                response.getBody().message()
        );

        verify(notificationService)
                .countUnreadNotifications(userId);
    }
}