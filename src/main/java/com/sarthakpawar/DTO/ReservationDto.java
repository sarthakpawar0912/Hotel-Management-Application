package com.sarthakpawar.DTO;

import com.sarthakpawar.ENUMS.ReservationStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationDto {

    private Long id;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Long price;

    private ReservationStatus reservationStatus;

    private Long roomId;

    private String roomType;

    private String roomName;

    private Long UserId;

    private String username;

}
