package com.sarthakpawar.DTO;

import com.sarthakpawar.ENUMS.ReservationStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservationDto {

    private Long id;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private Long price;

    private ReservationStatus reservationStatus;

    private Long roomId;

    private String roomType;

    private String roomName;

    private Long userId;

    private String userName;

    // Additional fields for booking
    private Integer nights;
    private Long discount;
    private Long totalAmount;
    private String promoCode;
    private String specialRequests;

    // Payment status
    private Boolean isPaid = false;

}
