package com.sarthakpawar.ENTITY;

import com.sarthakpawar.DTO.ReviewDto;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private Integer overallRating; // 1-5

    // Category ratings
    private Integer cleanlinessRating;
    private Integer serviceRating;
    private Integer amenitiesRating;
    private Integer valueForMoneyRating;
    private Integer locationRating;

    private String title;

    @Column(length = 2000)
    private String comment;

    private String pros;
    private String cons;

    private Boolean isAnonymous = false;
    private Boolean isVerifiedStay = false;
    private Boolean isApproved = false;

    // Admin response
    @Column(length = 1000)
    private String adminResponse;
    private LocalDateTime adminResponseAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public ReviewDto getReviewDto() {
        ReviewDto dto = new ReviewDto();
        dto.setId(id);
        if (reservation != null) {
            dto.setReservationId(reservation.getId());
        }
        dto.setRoomId(room.getId());
        dto.setRoomName(room.getName());
        dto.setUserId(user.getId());
        dto.setUserName(user.getName());
        dto.setOverallRating(overallRating);
        dto.setCleanlinessRating(cleanlinessRating);
        dto.setServiceRating(serviceRating);
        dto.setAmenitiesRating(amenitiesRating);
        dto.setValueForMoneyRating(valueForMoneyRating);
        dto.setLocationRating(locationRating);
        dto.setTitle(title);
        dto.setComment(comment);
        dto.setPros(pros);
        dto.setCons(cons);
        dto.setIsAnonymous(isAnonymous);
        dto.setIsVerifiedStay(isVerifiedStay);
        dto.setIsApproved(isApproved);
        dto.setAdminResponse(adminResponse);
        dto.setAdminResponseAt(adminResponseAt);
        dto.setCreatedAt(createdAt);
        return dto;
    }
}
