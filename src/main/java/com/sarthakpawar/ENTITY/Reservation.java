package com.sarthakpawar.ENTITY;

import com.sarthakpawar.DTO.ReservationDto;
import com.sarthakpawar.ENUMS.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private Long price;

    private Long discount;

    private Long totalAmount;

    private String promoCode;

    @Column(length = 1000)
    private String specialRequests;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name ="room_id",nullable = false )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name ="user_id",nullable = false )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

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

    public ReservationDto getReservationDto(){
        ReservationDto reservationDto=new ReservationDto();

        reservationDto.setId(id);
        reservationDto.setPrice(price);
        reservationDto.setCheckInDate(checkInDate);
        reservationDto.setCheckOutDate(checkOutDate);
        reservationDto.setCheckInTime(checkInTime);
        reservationDto.setCheckOutTime(checkOutTime);
        reservationDto.setReservationStatus(reservationStatus);

        reservationDto.setUserId(user.getId());
        reservationDto.setUserName(user.getName());

        reservationDto.setRoomId(room.getId());
        reservationDto.setRoomName(room.getName());
        reservationDto.setRoomType(room.getType());

        reservationDto.setDiscount(discount);
        reservationDto.setTotalAmount(totalAmount);
        reservationDto.setPromoCode(promoCode);
        reservationDto.setSpecialRequests(specialRequests);

        if (checkInDate != null && checkOutDate != null) {
            reservationDto.setNights((int) ChronoUnit.DAYS.between(checkInDate, checkOutDate));
        }

        return reservationDto;
    }

}