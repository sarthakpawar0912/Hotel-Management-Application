package com.sarthakpawar.CONTROLLER.ADMIN;

import com.sarthakpawar.DTO.NotificationDto;
import com.sarthakpawar.ENUMS.NotificationType;
import com.sarthakpawar.SERVICES.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
@CrossOrigin("*")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@RequestBody NotificationDto notificationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createNotification(notificationDto));
    }

    @PostMapping("/send")
    public ResponseEntity<NotificationDto> sendNotification(
            @RequestParam Long userId,
            @RequestParam NotificationType type,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam(required = false) String referenceType,
            @RequestParam(required = false) Long referenceId) {
        NotificationDto notification = notificationService.createNotification(
            userId, type, title, message, referenceType, referenceId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDto> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDto>> getNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUser(userId));
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCountByUser(@PathVariable Long userId) {
        Long count = notificationService.getUnreadCountByUser(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<NotificationDto> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PostMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsReadByUser(@PathVariable Long userId) {
        notificationService.markAllAsReadByUser(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationDto>> getNotificationsByType(@PathVariable NotificationType type) {
        return ResponseEntity.ok(notificationService.getNotificationsByType(type));
    }
}
