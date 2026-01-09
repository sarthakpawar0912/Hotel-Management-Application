package com.sarthakpawar.SERVICES.ADMIN.reports;

import com.sarthakpawar.ENTITY.Payment;
import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.ENTITY.Room;
import com.sarthakpawar.ENUMS.PaymentStatus;
import com.sarthakpawar.ENUMS.ReservationStatus;
import com.sarthakpawar.REPOSITORY.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportsServiceImpl implements ReportsService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final ReviewRepository reviewRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public Map<String, Object> getRevenueReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        List<Payment> payments = paymentRepository.findByDateRange(start, end);
        List<Payment> completedPayments = payments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.COMPLETED)
                .toList();

        long totalRevenue = completedPayments.stream()
                .mapToLong(Payment::getTotalAmount)
                .sum();

        long totalTax = completedPayments.stream()
                .mapToLong(p -> p.getCgst() + p.getSgst())
                .sum();

        // Daily breakdown
        Map<LocalDate, Long> dailyRevenue = new LinkedHashMap<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            LocalDate date = current;
            long dayRevenue = completedPayments.stream()
                    .filter(p -> p.getPaidAt() != null && p.getPaidAt().toLocalDate().equals(date))
                    .mapToLong(Payment::getTotalAmount)
                    .sum();
            dailyRevenue.put(date, dayRevenue);
            current = current.plusDays(1);
        }

        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalRevenue", totalRevenue);
        report.put("totalTax", totalTax);
        report.put("netRevenue", totalRevenue - totalTax);
        report.put("transactionCount", completedPayments.size());
        report.put("averageTransaction", completedPayments.isEmpty() ? 0 : totalRevenue / completedPayments.size());
        report.put("dailyBreakdown", dailyRevenue);

        return report;
    }

    @Override
    public Map<String, Object> getDailyRevenue(LocalDate date) {
        return getRevenueReport(date, date);
    }

    @Override
    public Map<String, Object> getMonthlyRevenue(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return getRevenueReport(startDate, endDate);
    }

    @Override
    public Map<String, Object> getYearlyRevenue(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return getRevenueReport(startDate, endDate);
    }

    @Override
    public Map<String, Object> getOccupancyReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        List<Room> rooms = roomRepository.findAll();
        List<Reservation> reservations = reservationRepository.findAll().stream()
                .filter(r -> !r.getCheckOutDate().isBefore(startDate) && !r.getCheckInDate().isAfter(endDate))
                .filter(r -> r.getReservationStatus() == ReservationStatus.CHECKED_IN ||
                           r.getReservationStatus() == ReservationStatus.CHECKED_OUT ||
                           r.getReservationStatus() == ReservationStatus.APPROVED)
                .toList();

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long totalRoomNights = rooms.size() * totalDays;
        long occupiedNights = 0;

        for (Reservation r : reservations) {
            LocalDate checkIn = r.getCheckInDate().isBefore(startDate) ? startDate : r.getCheckInDate();
            LocalDate checkOut = r.getCheckOutDate().isAfter(endDate) ? endDate : r.getCheckOutDate();
            occupiedNights += ChronoUnit.DAYS.between(checkIn, checkOut);
        }

        double occupancyRate = totalRoomNights > 0 ? (occupiedNights * 100.0 / totalRoomNights) : 0;

        // Daily occupancy
        Map<LocalDate, Double> dailyOccupancy = new LinkedHashMap<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            LocalDate date = current;
            long roomsOccupied = reservations.stream()
                    .filter(r -> !r.getCheckInDate().isAfter(date) && r.getCheckOutDate().isAfter(date))
                    .count();
            dailyOccupancy.put(date, rooms.isEmpty() ? 0 : (roomsOccupied * 100.0 / rooms.size()));
            current = current.plusDays(1);
        }

        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalRooms", rooms.size());
        report.put("totalRoomNights", totalRoomNights);
        report.put("occupiedNights", occupiedNights);
        report.put("occupancyRate", Math.round(occupancyRate * 100.0) / 100.0);
        report.put("availableNights", totalRoomNights - occupiedNights);
        report.put("dailyOccupancy", dailyOccupancy);

        return report;
    }

    @Override
    public Map<String, Object> getRoomTypeOccupancy(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        List<Room> rooms = roomRepository.findAll();
        List<Reservation> reservations = reservationRepository.findAll().stream()
                .filter(r -> !r.getCheckOutDate().isBefore(startDate) && !r.getCheckInDate().isAfter(endDate))
                .toList();

        Map<String, List<Room>> roomsByType = rooms.stream()
                .collect(Collectors.groupingBy(Room::getType));

        Map<String, Map<String, Object>> typeStats = new HashMap<>();

        for (Map.Entry<String, List<Room>> entry : roomsByType.entrySet()) {
            String type = entry.getKey();
            List<Room> typeRooms = entry.getValue();
            Set<Long> typeRoomIds = typeRooms.stream().map(Room::getId).collect(Collectors.toSet());

            long occupiedNights = reservations.stream()
                    .filter(r -> typeRoomIds.contains(r.getRoom().getId()))
                    .mapToLong(r -> {
                        LocalDate checkIn = r.getCheckInDate().isBefore(startDate) ? startDate : r.getCheckInDate();
                        LocalDate checkOut = r.getCheckOutDate().isAfter(endDate) ? endDate : r.getCheckOutDate();
                        return ChronoUnit.DAYS.between(checkIn, checkOut);
                    })
                    .sum();

            long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            long totalRoomNights = typeRooms.size() * totalDays;

            Map<String, Object> stats = new HashMap<>();
            stats.put("roomCount", typeRooms.size());
            stats.put("occupiedNights", occupiedNights);
            stats.put("occupancyRate", totalRoomNights > 0 ? Math.round((occupiedNights * 100.0 / totalRoomNights) * 100.0) / 100.0 : 0);

            typeStats.put(type, stats);
        }

        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("roomTypeStats", typeStats);

        return report;
    }

    @Override
    public Double getAverageOccupancyRate(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = getOccupancyReport(startDate, endDate);
        return (Double) report.get("occupancyRate");
    }

    @Override
    public Map<String, Object> getBookingReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        List<Reservation> reservations = reservationRepository.findAll().stream()
                .filter(r -> !r.getCheckInDate().isBefore(startDate) && !r.getCheckInDate().isAfter(endDate))
                .toList();

        long totalBookings = reservations.size();
        long confirmedBookings = reservations.stream()
                .filter(r -> r.getReservationStatus() == ReservationStatus.APPROVED ||
                           r.getReservationStatus() == ReservationStatus.CHECKED_IN ||
                           r.getReservationStatus() == ReservationStatus.CHECKED_OUT)
                .count();
        long cancelledBookings = reservations.stream()
                .filter(r -> r.getReservationStatus() == ReservationStatus.CANCELLED)
                .count();
        long pendingBookings = reservations.stream()
                .filter(r -> r.getReservationStatus() == ReservationStatus.PENDING)
                .count();

        long totalRevenue = reservations.stream()
                .filter(r -> r.getReservationStatus() != ReservationStatus.CANCELLED)
                .mapToLong(Reservation::getPrice)
                .sum();

        double averageBookingValue = confirmedBookings > 0 ? (double) totalRevenue / confirmedBookings : 0;

        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalBookings", totalBookings);
        report.put("confirmedBookings", confirmedBookings);
        report.put("cancelledBookings", cancelledBookings);
        report.put("pendingBookings", pendingBookings);
        report.put("cancellationRate", totalBookings > 0 ? Math.round((cancelledBookings * 100.0 / totalBookings) * 100.0) / 100.0 : 0);
        report.put("totalRevenue", totalRevenue);
        report.put("averageBookingValue", Math.round(averageBookingValue));

        return report;
    }

    @Override
    public Map<String, Object> getBookingSourceAnalysis(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();
        report.put("note", "Booking source tracking not implemented yet");
        report.put("direct", 100);
        return report;
    }

    @Override
    public Map<String, Object> getCancellationReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        List<Reservation> cancelledReservations = reservationRepository.findAll().stream()
                .filter(r -> r.getReservationStatus() == ReservationStatus.CANCELLED)
                .filter(r -> !r.getCheckInDate().isBefore(startDate) && !r.getCheckInDate().isAfter(endDate))
                .toList();

        long totalCancelled = cancelledReservations.size();
        long lostRevenue = cancelledReservations.stream()
                .mapToLong(Reservation::getPrice)
                .sum();

        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalCancellations", totalCancelled);
        report.put("lostRevenue", lostRevenue);

        return report;
    }

    @Override
    public Map<String, Object> getGuestReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        long totalGuests = guestRepository.count();
        long vipGuests = guestRepository.findByIsVipTrue().size();
        long newGuests = guestRepository.findAll().stream()
                .filter(g -> g.getCreatedAt() != null &&
                           !g.getCreatedAt().toLocalDate().isBefore(startDate) &&
                           !g.getCreatedAt().toLocalDate().isAfter(endDate))
                .count();

        report.put("totalGuests", totalGuests);
        report.put("vipGuests", vipGuests);
        report.put("newGuests", newGuests);

        return report;
    }

    @Override
    public Map<String, Object> getRepeatGuestAnalysis() {
        Map<String, Object> report = new HashMap<>();

        List<com.sarthakpawar.ENTITY.Guest> guests = guestRepository.findAll();
        long repeatGuests = guests.stream()
                .filter(g -> g.getTotalStays() != null && g.getTotalStays() > 1)
                .count();

        report.put("totalGuests", guests.size());
        report.put("repeatGuests", repeatGuests);
        report.put("repeatRate", guests.isEmpty() ? 0 : Math.round((repeatGuests * 100.0 / guests.size()) * 100.0) / 100.0);

        return report;
    }

    @Override
    public Map<String, Object> getGuestDemographics() {
        Map<String, Object> report = new HashMap<>();

        List<com.sarthakpawar.ENTITY.Guest> guests = guestRepository.findAll();

        Map<String, Long> byNationality = guests.stream()
                .filter(g -> g.getNationality() != null)
                .collect(Collectors.groupingBy(com.sarthakpawar.ENTITY.Guest::getNationality, Collectors.counting()));

        Map<String, Long> byLoyaltyTier = guests.stream()
                .filter(g -> g.getLoyaltyTier() != null)
                .collect(Collectors.groupingBy(g -> g.getLoyaltyTier().name(), Collectors.counting()));

        report.put("byNationality", byNationality);
        report.put("byLoyaltyTier", byLoyaltyTier);

        return report;
    }

    @Override
    public Map<String, Object> getRoomPerformanceReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        List<Room> rooms = roomRepository.findAll();
        List<Reservation> reservations = reservationRepository.findAll().stream()
                .filter(r -> !r.getCheckOutDate().isBefore(startDate) && !r.getCheckInDate().isAfter(endDate))
                .toList();

        List<Map<String, Object>> roomPerformance = new ArrayList<>();

        for (Room room : rooms) {
            Map<String, Object> perf = new HashMap<>();
            List<Reservation> roomReservations = reservations.stream()
                    .filter(r -> r.getRoom().getId().equals(room.getId()))
                    .toList();

            long totalBookings = roomReservations.size();
            long revenue = roomReservations.stream()
                    .mapToLong(Reservation::getPrice)
                    .sum();

            Double avgRating = reviewRepository.getAverageRatingByRoom(room.getId());

            perf.put("roomId", room.getId());
            perf.put("roomName", room.getName());
            perf.put("roomType", room.getType());
            perf.put("bookings", totalBookings);
            perf.put("revenue", revenue);
            perf.put("avgRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0);

            roomPerformance.add(perf);
        }

        // Sort by revenue
        roomPerformance.sort((a, b) -> Long.compare((Long) b.get("revenue"), (Long) a.get("revenue")));

        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("rooms", roomPerformance);

        return report;
    }

    @Override
    public Map<String, Object> getRoomMaintenanceReport() {
        Map<String, Object> report = new HashMap<>();

        List<Room> rooms = roomRepository.findAll();
        long availableRooms = rooms.stream().filter(Room::isAvailable).count();
        long unavailableRooms = rooms.size() - availableRooms;

        report.put("totalRooms", rooms.size());
        report.put("availableRooms", availableRooms);
        report.put("unavailableRooms", unavailableRooms);

        return report;
    }

    @Override
    public Map<String, Object> getTaxReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        Long cgst = paymentRepository.getTotalCgstByDateRange(start, end);
        Long sgst = paymentRepository.getTotalSgstByDateRange(start, end);

        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("cgst", cgst != null ? cgst : 0);
        report.put("sgst", sgst != null ? sgst : 0);
        report.put("totalTax", (cgst != null ? cgst : 0) + (sgst != null ? sgst : 0));

        return report;
    }

    @Override
    public Map<String, Object> getOutstandingPaymentsReport() {
        Map<String, Object> report = new HashMap<>();

        List<com.sarthakpawar.ENTITY.Invoice> unpaidInvoices = invoiceRepository.findWithOutstandingBalance();

        long totalOutstanding = unpaidInvoices.stream()
                .mapToLong(com.sarthakpawar.ENTITY.Invoice::getBalanceDue)
                .sum();

        report.put("unpaidInvoiceCount", unpaidInvoices.size());
        report.put("totalOutstanding", totalOutstanding);

        return report;
    }

    @Override
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        // Today's stats
        summary.put("todayCheckIns", reservationRepository.findAll().stream()
                .filter(r -> r.getCheckInDate().equals(today))
                .filter(r -> r.getReservationStatus() == ReservationStatus.APPROVED ||
                           r.getReservationStatus() == ReservationStatus.PENDING)
                .count());

        summary.put("todayCheckOuts", reservationRepository.findAll().stream()
                .filter(r -> r.getCheckOutDate().equals(today))
                .filter(r -> r.getReservationStatus() == ReservationStatus.CHECKED_IN)
                .count());

        // Occupancy
        long totalRooms = roomRepository.count();
        long occupiedRooms = reservationRepository.findAll().stream()
                .filter(r -> r.getReservationStatus() == ReservationStatus.CHECKED_IN)
                .count();
        summary.put("totalRooms", totalRooms);
        summary.put("occupiedRooms", occupiedRooms);
        summary.put("availableRooms", totalRooms - occupiedRooms);
        summary.put("occupancyRate", totalRooms > 0 ? Math.round((occupiedRooms * 100.0 / totalRooms) * 100.0) / 100.0 : 0);

        // Revenue
        Long totalRevenue = paymentRepository.getTotalRevenue();
        summary.put("totalRevenue", totalRevenue != null ? totalRevenue : 0);

        LocalDateTime monthStartTime = monthStart.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        Long monthlyRevenue = paymentRepository.getRevenueByDateRange(monthStartTime, todayEnd);
        summary.put("monthlyRevenue", monthlyRevenue != null ? monthlyRevenue : 0);

        // Counts
        summary.put("totalBookings", reservationRepository.count());
        summary.put("totalGuests", guestRepository.count());
        summary.put("pendingBookings", reservationRepository.findAll().stream()
                .filter(r -> r.getReservationStatus() == ReservationStatus.PENDING)
                .count());

        // Rating
        Double avgRating = reviewRepository.getOverallAverageRating();
        summary.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0);

        return summary;
    }

    @Override
    public Map<String, Object> getQuickStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalRooms", roomRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalBookings", reservationRepository.count());
        stats.put("totalRevenue", paymentRepository.getTotalRevenue());

        return stats;
    }
}
