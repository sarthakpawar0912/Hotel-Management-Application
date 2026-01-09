package com.sarthakpawar.SERVICES.notification;

import com.sarthakpawar.DTO.NotificationDto;
import com.sarthakpawar.ENUMS.NotificationType;

import java.util.List;

public interface NotificationService {

    NotificationDto createNotification(NotificationDto notificationDto);

    NotificationDto createNotification(Long userId, NotificationType type, String title, String message);

    NotificationDto createNotification(Long userId, NotificationType type, String title, String message, String referenceType, Long referenceId);

    NotificationDto getNotificationById(Long id);

    List<NotificationDto> getNotificationsByUser(Long userId);

    List<NotificationDto> getUnreadNotificationsByUser(Long userId);

    Long getUnreadCountByUser(Long userId);

    NotificationDto markAsRead(Long id);

    void markAllAsReadByUser(Long userId);

    void deleteNotification(Long id);

    List<NotificationDto> getNotificationsByType(NotificationType type);
}
