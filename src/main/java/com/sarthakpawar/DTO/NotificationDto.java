package com.sarthakpawar.DTO;

import com.sarthakpawar.ENUMS.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDto {

    private Long id;
    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private Boolean isRead;
    private Boolean emailSent;
    private Boolean smsSent;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private String referenceType;
    private Long referenceId;
}
