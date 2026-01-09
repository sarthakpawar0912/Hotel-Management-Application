package com.sarthakpawar.CONTROLLER.Customer;

import com.sarthakpawar.DTO.NotificationDto;
import com.sarthakpawar.ENTITY.User;
import com.sarthakpawar.SERVICES.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/notifications")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CustomerNotificationController {

    private final NotificationService notificationService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getMyNotifications() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUser(userId));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        Long userId = getCurrentUserId();
        Long count = notificationService.getUnreadCountByUser(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDto> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead() {
        Long userId = getCurrentUserId();
        notificationService.markAllAsReadByUser(userId);
        return ResponseEntity.ok().build();
    }
}
