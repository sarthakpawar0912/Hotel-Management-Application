package com.sarthakpawar.SERVICES.ADMIN.reports;

import java.time.LocalDate;
import java.util.Map;

public interface ReportsService {

    // Revenue Reports
    Map<String, Object> getRevenueReport(LocalDate startDate, LocalDate endDate);

    Map<String, Object> getDailyRevenue(LocalDate date);

    Map<String, Object> getMonthlyRevenue(int year, int month);

    Map<String, Object> getYearlyRevenue(int year);

    // Occupancy Reports
    Map<String, Object> getOccupancyReport(LocalDate startDate, LocalDate endDate);

    Map<String, Object> getRoomTypeOccupancy(LocalDate startDate, LocalDate endDate);

    Double getAverageOccupancyRate(LocalDate startDate, LocalDate endDate);

    // Booking Reports
    Map<String, Object> getBookingReport(LocalDate startDate, LocalDate endDate);

    Map<String, Object> getBookingSourceAnalysis(LocalDate startDate, LocalDate endDate);

    Map<String, Object> getCancellationReport(LocalDate startDate, LocalDate endDate);

    // Guest Reports
    Map<String, Object> getGuestReport(LocalDate startDate, LocalDate endDate);

    Map<String, Object> getRepeatGuestAnalysis();

    Map<String, Object> getGuestDemographics();

    // Room Reports
    Map<String, Object> getRoomPerformanceReport(LocalDate startDate, LocalDate endDate);

    Map<String, Object> getRoomMaintenanceReport();

    // Financial Reports
    Map<String, Object> getTaxReport(LocalDate startDate, LocalDate endDate);

    Map<String, Object> getOutstandingPaymentsReport();

    // Dashboard Summary
    Map<String, Object> getDashboardSummary();

    Map<String, Object> getQuickStats();
}
