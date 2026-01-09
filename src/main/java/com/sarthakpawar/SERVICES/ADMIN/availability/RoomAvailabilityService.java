package com.sarthakpawar.SERVICES.ADMIN.availability;

import com.sarthakpawar.DTO.RoomAvailabilityDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RoomAvailabilityService {

    // Availability Management
    RoomAvailabilityDto createOrUpdateAvailability(Long roomId, LocalDate date, RoomAvailabilityDto dto);

    void createAvailabilityForDateRange(Long roomId, LocalDate startDate, LocalDate endDate);

    RoomAvailabilityDto getAvailability(Long roomId, LocalDate date);

    List<RoomAvailabilityDto> getAvailabilityByRoom(Long roomId);

    List<RoomAvailabilityDto> getAvailabilityByRoomAndDateRange(Long roomId, LocalDate startDate, LocalDate endDate);

    List<RoomAvailabilityDto> getAvailabilityByDateRange(LocalDate startDate, LocalDate endDate);

    // Blocking
    void blockRoom(Long roomId, LocalDate startDate, LocalDate endDate, String reason);

    void unblockRoom(Long roomId, LocalDate startDate, LocalDate endDate);

    List<RoomAvailabilityDto> getBlockedDates();

    // Pricing
    void setCustomPrice(Long roomId, LocalDate date, Long price);

    void setCustomPriceForRange(Long roomId, LocalDate startDate, LocalDate endDate, Long price);

    void removeCustomPrice(Long roomId, LocalDate date);

    // Minimum Stay
    void setMinimumStay(Long roomId, LocalDate startDate, LocalDate endDate, Integer minStay);

    // Availability Check
    boolean isRoomAvailable(Long roomId, LocalDate startDate, LocalDate endDate);

    List<Long> getAvailableRoomIds(LocalDate startDate, LocalDate endDate);

    // Calendar View
    Map<LocalDate, List<RoomAvailabilityDto>> getCalendarView(LocalDate startDate, LocalDate endDate);

    Map<String, Object> getRoomCalendar(Long roomId, int year, int month);

    // Reservation Updates
    void markAsBooked(Long roomId, LocalDate startDate, LocalDate endDate, Long reservationId);

    void markAsAvailable(Long roomId, LocalDate startDate, LocalDate endDate);
}
