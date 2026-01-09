package com.sarthakpawar.SERVICES.ADMIN.checkinout;

import com.sarthakpawar.DTO.ReservationDto;
import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.ENTITY.Room;
import com.sarthakpawar.ENUMS.NotificationType;
import com.sarthakpawar.ENUMS.ReservationStatus;
import com.sarthakpawar.REPOSITORY.ReservationRepository;
import com.sarthakpawar.REPOSITORY.RoomRepository;
import com.sarthakpawar.SERVICES.notification.EmailService;
import com.sarthakpawar.SERVICES.notification.NotificationService;
import com.sarthakpawar.SERVICES.ADMIN.guest.GuestService;
import com.sarthakpawar.SERVICES.ADMIN.invoice.InvoiceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckInOutServiceImpl implements CheckInOutService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final GuestService guestService;
    private final InvoiceService invoiceService;

    @Override
    @Transactional
    public ReservationDto checkIn(Long reservationId, Map<String, Object> checkInDetails) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        if (reservation.getReservationStatus() != ReservationStatus.APPROVED &&
            reservation.getReservationStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Cannot check in. Current status: " + reservation.getReservationStatus());
        }

        // Update reservation status
        reservation.setReservationStatus(ReservationStatus.CHECKED_IN);
        reservation.setCheckInTime(LocalDateTime.now());

        // Store any additional check-in details if provided
        if (checkInDetails != null) {
            if (checkInDetails.containsKey("notes")) {
                // Could store notes if field exists
            }
        }

        Reservation updatedReservation = reservationRepository.save(reservation);

        // Mark room as occupied
        markRoomAsOccupied(reservation.getRoom().getId());

        // Send notification
        notificationService.createNotification(
            reservation.getUser().getId(),
            NotificationType.CHECK_IN_CONFIRMATION,
            "Welcome!",
            "You have successfully checked in to " + reservation.getRoom().getName(),
            "RESERVATION",
            reservationId
        );

        return getReservationDto(updatedReservation);
    }

    @Override
    public List<ReservationDto> getTodayCheckIns() {
        LocalDate today = LocalDate.now();
        return reservationRepository.findAll().stream()
                .filter(r -> r.getCheckInDate().equals(today))
                .filter(r -> r.getReservationStatus() == ReservationStatus.APPROVED ||
                           r.getReservationStatus() == ReservationStatus.PENDING)
                .map(this::getReservationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getUpcomingCheckIns(int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        return reservationRepository.findAll().stream()
                .filter(r -> !r.getCheckInDate().isBefore(today) && !r.getCheckInDate().isAfter(endDate))
                .filter(r -> r.getReservationStatus() == ReservationStatus.APPROVED ||
                           r.getReservationStatus() == ReservationStatus.PENDING)
                .map(this::getReservationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getPendingCheckIns() {
        LocalDate today = LocalDate.now();
        return reservationRepository.findAll().stream()
                .filter(r -> !r.getCheckInDate().isAfter(today))
                .filter(r -> r.getReservationStatus() == ReservationStatus.APPROVED ||
                           r.getReservationStatus() == ReservationStatus.PENDING)
                .map(this::getReservationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationDto checkOut(Long reservationId, Map<String, Object> checkOutDetails) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        if (reservation.getReservationStatus() != ReservationStatus.CHECKED_IN) {
            throw new RuntimeException("Guest is not checked in. Current status: " + reservation.getReservationStatus());
        }

        // Update reservation status
        reservation.setReservationStatus(ReservationStatus.CHECKED_OUT);
        reservation.setCheckOutTime(LocalDateTime.now());

        Reservation updatedReservation = reservationRepository.save(reservation);

        // Mark room for cleaning
        markRoomForCleaning(reservation.getRoom().getId());

        // Generate invoice if not exists
        try {
            invoiceService.generateInvoice(reservationId);
        } catch (Exception e) {
            // Invoice might already exist
        }

        // Update guest stats
        try {
            guestService.updateGuestStayStats(
                reservation.getUser().getId(),
                reservation.getPrice()
            );
        } catch (Exception e) {
            // Guest might not exist in guest table
        }

        // Send notification
        notificationService.createNotification(
            reservation.getUser().getId(),
            NotificationType.CHECK_OUT_CONFIRMATION,
            "Thank you for staying!",
            "You have successfully checked out. We hope to see you again!",
            "RESERVATION",
            reservationId
        );

        // Send review request email
        emailService.sendReviewRequest(reservation);

        return getReservationDto(updatedReservation);
    }

    @Override
    public List<ReservationDto> getTodayCheckOuts() {
        LocalDate today = LocalDate.now();
        return reservationRepository.findAll().stream()
                .filter(r -> r.getCheckOutDate().equals(today))
                .filter(r -> r.getReservationStatus() == ReservationStatus.CHECKED_IN)
                .map(this::getReservationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getUpcomingCheckOuts(int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        return reservationRepository.findAll().stream()
                .filter(r -> !r.getCheckOutDate().isBefore(today) && !r.getCheckOutDate().isAfter(endDate))
                .filter(r -> r.getReservationStatus() == ReservationStatus.CHECKED_IN)
                .map(this::getReservationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getPendingCheckOuts() {
        LocalDate today = LocalDate.now();
        return reservationRepository.findAll().stream()
                .filter(r -> !r.getCheckOutDate().isAfter(today))
                .filter(r -> r.getReservationStatus() == ReservationStatus.CHECKED_IN)
                .map(this::getReservationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationDto updateReservationStatus(Long reservationId, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        reservation.setReservationStatus(status);
        Reservation updatedReservation = reservationRepository.save(reservation);

        return getReservationDto(updatedReservation);
    }

    @Override
    @Transactional
    public ReservationDto markAsNoShow(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        reservation.setReservationStatus(ReservationStatus.NO_SHOW);
        Reservation updatedReservation = reservationRepository.save(reservation);

        // Send notification
        notificationService.createNotification(
            reservation.getUser().getId(),
            NotificationType.BOOKING_CANCELLATION,
            "Reservation Marked as No-Show",
            "Your reservation #" + reservationId + " has been marked as no-show.",
            "RESERVATION",
            reservationId
        );

        return getReservationDto(updatedReservation);
    }

    @Override
    @Transactional
    public ReservationDto cancelReservation(Long reservationId, String reason) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        Reservation updatedReservation = reservationRepository.save(reservation);

        // Send notification and email
        notificationService.createNotification(
            reservation.getUser().getId(),
            NotificationType.BOOKING_CANCELLATION,
            "Reservation Cancelled",
            "Your reservation #" + reservationId + " has been cancelled. Reason: " + reason,
            "RESERVATION",
            reservationId
        );

        emailService.sendBookingCancellation(reservation);

        return getReservationDto(updatedReservation);
    }

    @Override
    @Transactional
    public void markRoomAsOccupied(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
        room.setAvailable(false);
        roomRepository.save(room);
    }

    @Override
    @Transactional
    public void markRoomAsVacant(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
        room.setAvailable(true);
        roomRepository.save(room);
    }

    @Override
    @Transactional
    public void markRoomForCleaning(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
        room.setAvailable(false);
        // Room will be marked available after cleaning
        roomRepository.save(room);
    }

    @Override
    public Map<String, Object> getDailySummary(LocalDate date) {
        Map<String, Object> summary = new HashMap<>();

        List<Reservation> allReservations = reservationRepository.findAll();

        long checkInsToday = allReservations.stream()
                .filter(r -> r.getCheckInDate().equals(date))
                .filter(r -> r.getReservationStatus() == ReservationStatus.APPROVED ||
                           r.getReservationStatus() == ReservationStatus.PENDING ||
                           r.getReservationStatus() == ReservationStatus.CHECKED_IN)
                .count();

        long checkOutsToday = allReservations.stream()
                .filter(r -> r.getCheckOutDate().equals(date))
                .filter(r -> r.getReservationStatus() == ReservationStatus.CHECKED_IN ||
                           r.getReservationStatus() == ReservationStatus.CHECKED_OUT)
                .count();

        long currentlyOccupied = allReservations.stream()
                .filter(r -> r.getReservationStatus() == ReservationStatus.CHECKED_IN)
                .count();

        long totalRooms = roomRepository.count();

        summary.put("date", date);
        summary.put("checkInsExpected", checkInsToday);
        summary.put("checkOutsExpected", checkOutsToday);
        summary.put("currentlyOccupied", currentlyOccupied);
        summary.put("totalRooms", totalRooms);
        summary.put("availableRooms", totalRooms - currentlyOccupied);
        summary.put("occupancyRate", totalRooms > 0 ? (currentlyOccupied * 100.0 / totalRooms) : 0);

        return summary;
    }

    @Override
    public Map<String, Object> getOccupancyStatus() {
        Map<String, Object> status = new HashMap<>();

        long totalRooms = roomRepository.count();
        long availableRooms = roomRepository.findAll().stream()
                .filter(Room::isAvailable)
                .count();
        long occupiedRooms = totalRooms - availableRooms;

        status.put("totalRooms", totalRooms);
        status.put("occupiedRooms", occupiedRooms);
        status.put("availableRooms", availableRooms);
        status.put("occupancyRate", totalRooms > 0 ? (occupiedRooms * 100.0 / totalRooms) : 0);

        return status;
    }

    @Override
    public List<ReservationDto> getCurrentlyCheckedInGuests() {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getReservationStatus() == ReservationStatus.CHECKED_IN)
                .map(this::getReservationDto)
                .collect(Collectors.toList());
    }

    private ReservationDto getReservationDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setRoomId(reservation.getRoom().getId());
        dto.setRoomType(reservation.getRoom().getType());
        dto.setRoomName(reservation.getRoom().getName());
        dto.setUserId(reservation.getUser().getId());
        dto.setUserName(reservation.getUser().getName());
        dto.setCheckInDate(reservation.getCheckInDate());
        dto.setCheckOutDate(reservation.getCheckOutDate());
        dto.setPrice(reservation.getPrice());
        dto.setReservationStatus(reservation.getReservationStatus());
        return dto;
    }
}
