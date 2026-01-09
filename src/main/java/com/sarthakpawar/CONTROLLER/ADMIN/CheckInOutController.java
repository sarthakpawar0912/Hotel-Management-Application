package com.sarthakpawar.CONTROLLER.ADMIN;

import com.sarthakpawar.DTO.ReservationDto;
import com.sarthakpawar.ENUMS.ReservationStatus;
import com.sarthakpawar.SERVICES.ADMIN.checkinout.CheckInOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/check-in-out")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CheckInOutController {

    private final CheckInOutService checkInOutService;

    // Check-in Operations
    @PostMapping("/check-in/{reservationId}")
    public ResponseEntity<ReservationDto> checkIn(
            @PathVariable Long reservationId,
            @RequestBody(required = false) Map<String, Object> checkInDetails) {
        return ResponseEntity.ok(checkInOutService.checkIn(reservationId, checkInDetails));
    }

    @GetMapping("/today-check-ins")
    public ResponseEntity<List<ReservationDto>> getTodayCheckIns() {
        return ResponseEntity.ok(checkInOutService.getTodayCheckIns());
    }

    @GetMapping("/checkins/today")
    public ResponseEntity<List<ReservationDto>> getTodayCheckInsAlt() {
        return ResponseEntity.ok(checkInOutService.getTodayCheckIns());
    }

    @GetMapping("/upcoming-check-ins")
    public ResponseEntity<List<ReservationDto>> getUpcomingCheckIns(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(checkInOutService.getUpcomingCheckIns(days));
    }

    @GetMapping("/pending-check-ins")
    public ResponseEntity<List<ReservationDto>> getPendingCheckIns() {
        return ResponseEntity.ok(checkInOutService.getPendingCheckIns());
    }

    // Check-out Operations
    @PostMapping("/check-out/{reservationId}")
    public ResponseEntity<ReservationDto> checkOut(
            @PathVariable Long reservationId,
            @RequestBody(required = false) Map<String, Object> checkOutDetails) {
        return ResponseEntity.ok(checkInOutService.checkOut(reservationId, checkOutDetails));
    }

    @GetMapping("/today-check-outs")
    public ResponseEntity<List<ReservationDto>> getTodayCheckOuts() {
        return ResponseEntity.ok(checkInOutService.getTodayCheckOuts());
    }

    @GetMapping("/checkouts/today")
    public ResponseEntity<List<ReservationDto>> getTodayCheckOutsAlt() {
        return ResponseEntity.ok(checkInOutService.getTodayCheckOuts());
    }

    @GetMapping("/upcoming-check-outs")
    public ResponseEntity<List<ReservationDto>> getUpcomingCheckOuts(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(checkInOutService.getUpcomingCheckOuts(days));
    }

    @GetMapping("/pending-check-outs")
    public ResponseEntity<List<ReservationDto>> getPendingCheckOuts() {
        return ResponseEntity.ok(checkInOutService.getPendingCheckOuts());
    }

    // Status Operations
    @PutMapping("/{reservationId}/status")
    public ResponseEntity<ReservationDto> updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestParam ReservationStatus status) {
        return ResponseEntity.ok(checkInOutService.updateReservationStatus(reservationId, status));
    }

    @PostMapping("/no-show/{reservationId}")
    public ResponseEntity<ReservationDto> markAsNoShow(@PathVariable Long reservationId) {
        return ResponseEntity.ok(checkInOutService.markAsNoShow(reservationId));
    }

    @PostMapping("/cancel/{reservationId}")
    public ResponseEntity<ReservationDto> cancelReservation(
            @PathVariable Long reservationId,
            @RequestParam String reason) {
        return ResponseEntity.ok(checkInOutService.cancelReservation(reservationId, reason));
    }

    // Room Operations
    @PostMapping("/room/{roomId}/occupied")
    public ResponseEntity<Void> markRoomAsOccupied(@PathVariable Long roomId) {
        checkInOutService.markRoomAsOccupied(roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/{roomId}/vacant")
    public ResponseEntity<Void> markRoomAsVacant(@PathVariable Long roomId) {
        checkInOutService.markRoomAsVacant(roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/{roomId}/cleaning")
    public ResponseEntity<Void> markRoomForCleaning(@PathVariable Long roomId) {
        checkInOutService.markRoomForCleaning(roomId);
        return ResponseEntity.ok().build();
    }

    // Summary & Reports
    @GetMapping("/daily-summary")
    public ResponseEntity<Map<String, Object>> getDailySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(checkInOutService.getDailySummary(date));
    }

    @GetMapping("/summary/daily")
    public ResponseEntity<Map<String, Object>> getDailySummaryAlt(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(checkInOutService.getDailySummary(date));
    }

    @GetMapping("/summary/today")
    public ResponseEntity<Map<String, Object>> getTodaySummary() {
        return ResponseEntity.ok(checkInOutService.getDailySummary(LocalDate.now()));
    }

    @GetMapping("/occupancy-status")
    public ResponseEntity<Map<String, Object>> getOccupancyStatus() {
        return ResponseEntity.ok(checkInOutService.getOccupancyStatus());
    }

    @GetMapping("/occupancy")
    public ResponseEntity<Map<String, Object>> getOccupancyStatusAlt() {
        return ResponseEntity.ok(checkInOutService.getOccupancyStatus());
    }

    @GetMapping("/currently-checked-in")
    public ResponseEntity<List<ReservationDto>> getCurrentlyCheckedInGuests() {
        return ResponseEntity.ok(checkInOutService.getCurrentlyCheckedInGuests());
    }

    @GetMapping("/guests/current")
    public ResponseEntity<List<ReservationDto>> getCurrentlyCheckedInGuestsAlt() {
        return ResponseEntity.ok(checkInOutService.getCurrentlyCheckedInGuests());
    }
}
