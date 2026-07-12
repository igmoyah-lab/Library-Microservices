package com.library.notifications.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.library.notifications.entity.Notification;

@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void findByUserIdOrderByCreatedAtDesc_shouldReturnNotificationsOrderedByNewest() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        Notification oldestNotification = createNotification(
                userId,
                "Primera notificación",
                false,
                LocalDateTime.now().minusDays(2)
        );

        Notification newestNotification = createNotification(
                userId,
                "Segunda notificación",
                false,
                LocalDateTime.now()
        );

        Notification otherUserNotification = createNotification(
                otherUserId,
                "Notificación de otro usuario",
                false,
                LocalDateTime.now().plusHours(1)
        );

        notificationRepository.save(oldestNotification);
        notificationRepository.save(newestNotification);
        notificationRepository.save(otherUserNotification);
        notificationRepository.flush();

        List<Notification> result =
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(userId);

        assertEquals(2, result.size());

        assertEquals(
                newestNotification.getId(),
                result.get(0).getId()
        );

        assertEquals(
                oldestNotification.getId(),
                result.get(1).getId()
        );

        assertTrue(
                result.stream()
                        .allMatch(notification ->
                                notification.getUserId().equals(userId)
                        )
        );
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_shouldReturnEmptyList_whenUserHasNoNotifications() {
        UUID userId = UUID.randomUUID();

        List<Notification> result =
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void countByUserIdAndReadFalse_shouldReturnUnreadNotificationsCount() {
        UUID userId = UUID.randomUUID();

        Notification unreadNotificationOne = createNotification(
                userId,
                "Notificación no leída 1",
                false,
                LocalDateTime.now().minusMinutes(10)
        );

        Notification unreadNotificationTwo = createNotification(
                userId,
                "Notificación no leída 2",
                false,
                LocalDateTime.now().minusMinutes(5)
        );

        Notification readNotification = createNotification(
                userId,
                "Notificación leída",
                true,
                LocalDateTime.now()
        );

        notificationRepository.saveAll(
                List.of(
                        unreadNotificationOne,
                        unreadNotificationTwo,
                        readNotification
                )
        );

        notificationRepository.flush();

        long result =
                notificationRepository
                        .countByUserIdAndReadFalse(userId);

        assertEquals(2L, result);
    }

    @Test
    void countByUserIdAndReadFalse_shouldOnlyCountNotificationsFromSpecifiedUser() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        Notification userUnreadNotification = createNotification(
                userId,
                "Notificación del usuario",
                false,
                LocalDateTime.now()
        );

        Notification otherUserUnreadNotification = createNotification(
                otherUserId,
                "Notificación de otro usuario",
                false,
                LocalDateTime.now()
        );

        notificationRepository.saveAll(
                List.of(
                        userUnreadNotification,
                        otherUserUnreadNotification
                )
        );

        notificationRepository.flush();

        long result =
                notificationRepository
                        .countByUserIdAndReadFalse(userId);

        assertEquals(1L, result);
    }

    @Test
    void countByUserIdAndReadFalse_shouldReturnZero_whenAllNotificationsAreRead() {
        UUID userId = UUID.randomUUID();

        Notification readNotification = createNotification(
                userId,
                "Notificación leída",
                true,
                LocalDateTime.now()
        );

        notificationRepository.save(readNotification);
        notificationRepository.flush();

        long result =
                notificationRepository
                        .countByUserIdAndReadFalse(userId);

        assertEquals(0L, result);
    }

    private Notification createNotification(
            UUID userId,
            String message,
            boolean read,
            LocalDateTime createdAt
    ) {
        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setRead(read);
        notification.setCreatedAt(createdAt);

        return notification;
    }
}