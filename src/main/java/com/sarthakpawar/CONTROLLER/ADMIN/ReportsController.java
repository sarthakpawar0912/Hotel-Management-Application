package com.sarthakpawar.CONTROLLER.ADMIN;

import com.sarthakpawar.SERVICES.ADMIN.reports.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReportsController {

    private final ReportsService reportsService;

    // Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        return ResponseEntity.ok(reportsService.getDashboardSummary());
    }

    @GetMapping("/quick-stats")
    public ResponseEntity<Map<String, Object>> getQuickStats() {
        return ResponseEntity.ok(reportsService.getQuickStats());
    }

    // Revenue Reports
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportsService.getRevenueReport(startDate, endDate));
    }

    @GetMapping("/revenue/daily")
    public ResponseEntity<Map<String, Object>> getDailyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportsService.getDailyRevenue(date));
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyRevenue(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(reportsService.getMonthlyRevenue(year, month));
    }

    @GetMapping("/revenue/yearly")
    public ResponseEntity<Map<String, Object>> getYearlyRevenue(@RequestParam int year) {
        return ResponseEntity.ok(reportsService.getYearlyRevenue(year));
    }

    // Occupancy Reports
    @GetMapping("/occupancy")
    public ResponseEntity<Map<String, Object>> getOccupancyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportsService.getOccupancyReport(startDate, endDate));
    }

    @GetMapping("/occupancy/room-type")
    public ResponseEntity<Map<String, Object>> getRoomTypeOccupancy(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportsService.getRoomTypeOccupancy(startDate, endDate));
    }

    @GetMapping("/occupancy/average")
    public ResponseEntity<Map<String, Object>> getAverageOccupancy(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Double rate = reportsService.getAverageOccupancyRate(startDate, endDate);
        return ResponseEntity.ok(Map.of("averageOccupancyRate", rate));
    }

    // Booking Reports
    @GetMapping("/bookings")
    public ResponseEntity<Map<String, Object>> getBookingReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportsService.getBookingReport(startDate, endDate));
    }

    @GetMapping("/bookings/sources")
    public ResponseEntity<Map<String, Object>> getBookingSourceAnalysis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportsService.getBookingSourceAnalysis(startDate, endDate));
    }

    @GetMapping("/cancellations")
    public ResponseEntity<Map<String, Object>> getCancellationReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportsService.getCancellationReport(startDate, endDate));
    }

    // Guest Reports
    @GetMapping("/guests")
    public ResponseEntity<Map<String, Object>> getGuestReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportsService.getGuestReport(startDate, endDate));
    }

    @GetMapping("/guests/repeat")
    public ResponseEntity<Map<String, Object>> getRepeatGuestAnalysis() {
        return ResponseEntity.ok(reportsService.getRepeatGuestAnalysis());
    }

    @GetMapping("/guests/demographics")
    public ResponseEntity<Map<String, Object>> getGuestDemographics() {
        return ResponseEntity.ok(reportsService.getGuestDemographics());
    }

    // Room Reports
    @GetMapping("/rooms/performance")
    public ResponseEntity<Map<String, Object>> getRoomPerformanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportsService.getRoomPerformanceReport(startDate, endDate));
    }

    @GetMapping("/rooms/maintenance")
    public ResponseEntity<Map<String, Object>> getRoomMaintenanceReport() {
        return ResponseEntity.ok(reportsService.getRoomMaintenanceReport());
    }

    // Financial Reports
    @GetMapping("/taxes")
    public ResponseEntity<Map<String, Object>> getTaxReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportsService.getTaxReport(startDate, endDate));
    }

    @GetMapping("/outstanding")
    public ResponseEntity<Map<String, Object>> getOutstandingPaymentsReport() {
        return ResponseEntity.ok(reportsService.getOutstandingPaymentsReport());
    }
}
