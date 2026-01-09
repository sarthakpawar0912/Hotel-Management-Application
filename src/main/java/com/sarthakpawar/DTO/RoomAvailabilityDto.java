package com.sarthakpawar.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RoomAvailabilityDto {

    private Long id;
    private Long roomId;
    private String roomName;
    private LocalDate date;
    private Boolean isAvailable;
    private Boolean isBlocked;
    private String blockReason;
    private Long customPrice;
    private Integer minStay;
    private Long reservationId;
}
