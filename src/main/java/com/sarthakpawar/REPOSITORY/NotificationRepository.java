package com.sarthakpawar.REPOSITORY;

import com.sarthakpawar.ENTITY.Notification;
import com.sarthakpawar.ENUMS.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndIsReadFalse(Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByType(NotificationType type);

    List<Notification> findByEmailSentFalse();

    List<Notification> findBySmsSentFalse();

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadByUser(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    void markAllAsReadByUser(@Param("userId") Long userId);

    List<Notification> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}
