package com.sarthakpawar.ENTITY;

import com.sarthakpawar.DTO.NotificationDto;
import com.sarthakpawar.ENUMS.NotificationType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String message;

    private String email;
    private String phone;

    private Boolean isRead = false;
    private Boolean emailSent = false;
    private Boolean smsSent = false;

    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    // Reference to related entity
    private String referenceType; // RESERVATION, PAYMENT, REVIEW, etc.
    private Long referenceId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public NotificationDto getNotificationDto() {
        NotificationDto dto = new NotificationDto();
        dto.setId(id);
        if (user != null) {
            dto.setUserId(user.getId());
        }
        dto.setType(type);
        dto.setTitle(title);
        dto.setMessage(message);
        dto.setIsRead(isRead);
        dto.setEmailSent(emailSent);
        dto.setSmsSent(smsSent);
        dto.setSentAt(sentAt);
        dto.setReadAt(readAt);
        dto.setCreatedAt(createdAt);
        dto.setReferenceType(referenceType);
        dto.setReferenceId(referenceId);
        return dto;
    }
}
