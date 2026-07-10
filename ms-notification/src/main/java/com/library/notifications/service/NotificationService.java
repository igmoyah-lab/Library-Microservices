package com.library.notifications.service;

import java.util.List;
import java.util.UUID;

import com.library.notifications.dto.ApiResponse;
import com.library.notifications.dto.NotificationRequest;
import com.library.notifications.dto.NotificationResponse;

public interface NotificationService {

    ApiResponse<NotificationResponse> createNotification(
            NotificationRequest notificationRequest
    );

    ApiResponse<List<NotificationResponse>> getNotificationsByUserId(
            UUID userId
    );
}
