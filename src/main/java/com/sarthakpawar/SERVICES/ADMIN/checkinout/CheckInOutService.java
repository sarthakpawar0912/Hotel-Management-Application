package com.sarthakpawar.SERVICES.ADMIN.checkinout;

import com.sarthakpawar.DTO.ReservationDto;
import com.sarthakpawar.ENUMS.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CheckInOutService {

    // Check-in Operations
    ReservationDto checkIn(Long reservationId, Map<String, Object> checkInDetails);

    List<ReservationDto> getTodayCheckIns();

    List<ReservationDto> getUpcomingCheckIns(int days);

    List<ReservationDto> getPendingCheckIns();

    // Check-out Operations
    ReservationDto checkOut(Long reservationId, Map<String, Object> checkOutDetails);

    List<ReservationDto> getTodayCheckOuts();

    List<ReservationDto> getUpcomingCheckOuts(int days);

    List<ReservationDto> getPendingCheckOuts();

    // Status Operations
    ReservationDto updateReservationStatus(Long reservationId, ReservationStatus status);

    ReservationDto markAsNoShow(Long reservationId);

    ReservationDto cancelReservation(Long reservationId, String reason);

    // Room Operations
    void markRoomAsOccupied(Long roomId);

    void markRoomAsVacant(Long roomId);

    void markRoomForCleaning(Long roomId);

    // Summary & Reports
    Map<String, Object> getDailySummary(LocalDate date);

    Map<String, Object> getOccupancyStatus();

    List<ReservationDto> getCurrentlyCheckedInGuests();
}
