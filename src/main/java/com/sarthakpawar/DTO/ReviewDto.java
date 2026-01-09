package com.sarthakpawar.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDto {

    private Long id;
    private Long reservationId;
    private Long roomId;
    private String roomName;
    private Long userId;
    private String userName;
    private Integer overallRating;
    private Integer cleanlinessRating;
    private Integer serviceRating;
    private Integer amenitiesRating;
    private Integer valueForMoneyRating;
    private Integer locationRating;
    private String title;
    private String comment;
    private String pros;
    private String cons;
    private Boolean isAnonymous;
    private Boolean isVerifiedStay;
    private Boolean isApproved;
    private String adminResponse;
    private LocalDateTime adminResponseAt;
    private LocalDateTime createdAt;
}
