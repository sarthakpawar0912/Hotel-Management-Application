package com.sarthakpawar.SERVICES.notification;

import com.sarthakpawar.DTO.NotificationDto;
import com.sarthakpawar.ENTITY.Notification;
import com.sarthakpawar.ENTITY.User;
import com.sarthakpawar.ENUMS.NotificationType;
import com.sarthakpawar.REPOSITORY.NotificationRepository;
import com.sarthakpawar.REPOSITORY.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public NotificationDto createNotification(NotificationDto notificationDto) {
        Notification notification = new Notification();

        if (notificationDto.getUserId() != null) {
            User user = userRepository.findById(notificationDto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            notification.setUser(user);
        }

        notification.setType(notificationDto.getType());
        notification.setTitle(notificationDto.getTitle());
        notification.setMessage(notificationDto.getMessage());
        notification.setReferenceType(notificationDto.getReferenceType());
        notification.setReferenceId(notificationDto.getReferenceId());
        notification.setIsRead(false);

        Notification savedNotification = notificationRepository.save(notification);
        return savedNotification.getNotificationDto();
    }

    @Override
    @Transactional
    public NotificationDto createNotification(Long userId, NotificationType type, String title, String message) {
        return createNotification(userId, type, title, message, null, null);
    }

    @Override
    @Transactional
    public NotificationDto createNotification(Long userId, NotificationType type, String title, String message, String referenceType, Long referenceId) {
        Notification notification = new Notification();

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            notification.setUser(user);
        }

        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReferenceType(referenceType);
        notification.setReferenceId(referenceId);
        notification.setIsRead(false);

        Notification savedNotification = notificationRepository.save(notification);
        return savedNotification.getNotificationDto();
    }

    @Override
    public NotificationDto getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        return notification.getNotificationDto();
    }

    @Override
    public List<NotificationDto> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(Notification::getNotificationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDto> getUnreadNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId).stream()
                .map(Notification::getNotificationDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long getUnreadCountByUser(Long userId) {
        return notificationRepository.countUnreadByUser(userId);
    }

    @Override
    @Transactional
    public NotificationDto markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());

        Notification updatedNotification = notificationRepository.save(notification);
        return updatedNotification.getNotificationDto();
    }

    @Override
    @Transactional
    public void markAllAsReadByUser(Long userId) {
        notificationRepository.markAllAsReadByUser(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new EntityNotFoundException("Notification not found");
        }
        notificationRepository.deleteById(id);
    }

    @Override
    public List<NotificationDto> getNotificationsByType(NotificationType type) {
        return notificationRepository.findByType(type).stream()
                .map(Notification::getNotificationDto)
                .collect(Collectors.toList());
    }
}
