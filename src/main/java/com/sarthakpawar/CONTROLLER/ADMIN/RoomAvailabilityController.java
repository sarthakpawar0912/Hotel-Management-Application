package com.sarthakpawar.CONTROLLER.ADMIN;

import com.sarthakpawar.DTO.RoomAvailabilityDto;
import com.sarthakpawar.SERVICES.ADMIN.availability.RoomAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/availability")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RoomAvailabilityController {

    private final RoomAvailabilityService roomAvailabilityService;

    @PostMapping("/room/{roomId}")
    public ResponseEntity<RoomAvailabilityDto> createOrUpdateAvailability(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody RoomAvailabilityDto dto) {
        return ResponseEntity.ok(roomAvailabilityService.createOrUpdateAvailability(roomId, date, dto));
    }

    @PostMapping("/room/{roomId}/initialize")
    public ResponseEntity<Void> createAvailabilityForDateRange(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        roomAvailabilityService.createAvailabilityForDateRange(roomId, startDate, endDate);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<RoomAvailabilityDto>> getAvailabilityByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomAvailabilityService.getAvailabilityByRoom(roomId));
    }

    @GetMapping("/room/{roomId}/date")
    public ResponseEntity<RoomAvailabilityDto> getAvailability(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(roomAvailabilityService.getAvailability(roomId, date));
    }

    @GetMapping("/room/{roomId}/range")
    public ResponseEntity<List<RoomAvailabilityDto>> getAvailabilityByRoomAndDateRange(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(roomAvailabilityService.getAvailabilityByRoomAndDateRange(roomId, startDate, endDate));
    }

    @GetMapping("/range")
    public ResponseEntity<List<RoomAvailabilityDto>> getAvailabilityByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(roomAvailabilityService.getAvailabilityByDateRange(startDate, endDate));
    }

    // Blocking
    @PostMapping("/room/{roomId}/block")
    public ResponseEntity<Void> blockRoom(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String reason) {
        roomAvailabilityService.blockRoom(roomId, startDate, endDate, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/{roomId}/unblock")
    public ResponseEntity<Void> unblockRoom(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        roomAvailabilityService.unblockRoom(roomId, startDate, endDate);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/blocked")
    public ResponseEntity<List<RoomAvailabilityDto>> getBlockedDates() {
        return ResponseEntity.ok(roomAvailabilityService.getBlockedDates());
    }

    // Pricing
    @PostMapping("/room/{roomId}/price")
    public ResponseEntity<Void> setCustomPrice(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long price) {
        roomAvailabilityService.setCustomPrice(roomId, date, price);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/{roomId}/price/range")
    public ResponseEntity<Void> setCustomPriceForRange(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam Long price) {
        roomAvailabilityService.setCustomPriceForRange(roomId, startDate, endDate, price);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/room/{roomId}/price")
    public ResponseEntity<Void> removeCustomPrice(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        roomAvailabilityService.removeCustomPrice(roomId, date);
        return ResponseEntity.ok().build();
    }

    // Minimum Stay
    @PostMapping("/room/{roomId}/min-stay")
    public ResponseEntity<Void> setMinimumStay(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam Integer minStay) {
        roomAvailabilityService.setMinimumStay(roomId, startDate, endDate, minStay);
        return ResponseEntity.ok().build();
    }

    // Availability Check
    @GetMapping("/room/{roomId}/check")
    public ResponseEntity<Map<String, Boolean>> isRoomAvailable(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        boolean available = roomAvailabilityService.isRoomAvailable(roomId, startDate, endDate);
        return ResponseEntity.ok(Map.of("available", available));
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<List<Long>> getAvailableRoomIds(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(roomAvailabilityService.getAvailableRoomIds(startDate, endDate));
    }

    // Calendar View
    @GetMapping("/calendar")
    public ResponseEntity<Map<LocalDate, List<RoomAvailabilityDto>>> getCalendarView(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(roomAvailabilityService.getCalendarView(startDate, endDate));
    }

    @GetMapping("/room/{roomId}/calendar")
    public ResponseEntity<Map<String, Object>> getRoomCalendar(
            @PathVariable Long roomId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(roomAvailabilityService.getRoomCalendar(roomId, year, month));
    }

    // Reservation Updates
    @PostMapping("/room/{roomId}/book")
    public ResponseEntity<Void> markAsBooked(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long reservationId) {
        roomAvailabilityService.markAsBooked(roomId, startDate, endDate, reservationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/{roomId}/release")
    public ResponseEntity<Void> markAsAvailable(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        roomAvailabilityService.markAsAvailable(roomId, startDate, endDate);
        return ResponseEntity.ok().build();
    }
}
