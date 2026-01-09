package com.sarthakpawar.SERVICES.CUSTOMER.bookings;

import com.sarthakpawar.DTO.ReservationDto;
import com.sarthakpawar.DTO.ReservationResponseDto;
import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.ENTITY.Room;
import com.sarthakpawar.ENTITY.User;
import com.sarthakpawar.ENUMS.ReservationStatus;
import com.sarthakpawar.REPOSITORY.PaymentRepository;
import com.sarthakpawar.REPOSITORY.ReservationRepository;
import com.sarthakpawar.REPOSITORY.RoomRepository;
import com.sarthakpawar.REPOSITORY.UserRepository;
import com.sarthakpawar.SERVICES.notification.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements  BookingService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EmailService emailService;

    public static final int SEARCH_RESULT_PER_PAGE=4;

    public boolean postReservation(ReservationDto reservationDto){
        Optional<User> optionalUser=userRepository.findById(reservationDto.getUserId());
        Optional<Room> optionalRoom=roomRepository.findById(reservationDto.getRoomId());
        if(optionalRoom.isPresent() && optionalRoom.isPresent()){
            Reservation reservation=new Reservation();
            reservation.setRoom(optionalRoom.get());
            reservation.setUser(optionalUser.get());
            reservation.setCheckInDate(reservationDto.getCheckInDate());
            reservation.setCheckOutDate(reservationDto.getCheckOutDate());
            reservation.setReservationStatus(ReservationStatus.PENDING);
            Long days= ChronoUnit.DAYS.between(reservationDto.getCheckInDate(), reservationDto.getCheckOutDate());
            reservation.setPrice(optionalRoom.get().getPrice()*days);
            reservationRepository.save(reservation);
            return  true;
        }
        return false;
    }

    public ReservationResponseDto getAllReservationByUserId(Long userId, int pageNumber){
        Pageable pageable= PageRequest.of(pageNumber, SEARCH_RESULT_PER_PAGE);
        Page<Reservation> reservationPage=reservationRepository.findAllByUserId(pageable,userId);
        ReservationResponseDto reservationResponseDto=new ReservationResponseDto();
        reservationResponseDto.setReservationDtoList(
            reservationPage.stream()
                .map(reservation -> {
                    ReservationDto dto = reservation.getReservationDto();
                    // Check if payment exists for this reservation
                    dto.setIsPaid(paymentRepository.existsCompletedPaymentByReservationId(reservation.getId()));
                    return dto;
                })
                .collect(Collectors.toList())
        );
        reservationResponseDto.setPageNumber((reservationPage.getPageable().getPageNumber()));
        reservationResponseDto.setTotalPages(reservationPage.getTotalPages());
        return reservationResponseDto;
    }

    @Override
    public boolean cancelReservation(Long reservationId, Long userId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);

        if (optionalReservation.isEmpty()) {
            log.warn("Reservation not found with id: {}", reservationId);
            return false;
        }

        Reservation reservation = optionalReservation.get();

        // Verify the reservation belongs to the user
        if (!reservation.getUser().getId().equals(userId)) {
            log.warn("User {} attempted to cancel reservation {} that doesn't belong to them", userId, reservationId);
            return false;
        }

        // Check if the reservation can be cancelled (only PENDING or APPROVED can be cancelled)
        List<ReservationStatus> cancellableStatuses = Arrays.asList(
            ReservationStatus.PENDING,
            ReservationStatus.APPROVED
        );

        if (!cancellableStatuses.contains(reservation.getReservationStatus())) {
            log.warn("Cannot cancel reservation {} with status: {}", reservationId, reservation.getReservationStatus());
            return false;
        }

        // Check if already paid
        boolean isPaid = paymentRepository.existsCompletedPaymentByReservationId(reservationId);
        if (isPaid) {
            log.warn("Cannot cancel paid reservation: {}", reservationId);
            return false;
        }

        // Check if check-in date has not passed
        if (reservation.getCheckInDate().isBefore(LocalDate.now()) || reservation.getCheckInDate().isEqual(LocalDate.now())) {
            log.warn("Cannot cancel reservation {} as check-in date has passed or is today", reservationId);
            return false;
        }

        // Update status to CANCELLED
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        // Send cancellation email asynchronously
        try {
            emailService.sendBookingCancellation(reservation);
            log.info("Booking cancellation email sent for reservation: {}", reservationId);
        } catch (Exception e) {
            log.error("Failed to send cancellation email for reservation {}: {}", reservationId, e.getMessage());
            // Don't fail the cancellation if email fails
        }

        log.info("Reservation {} cancelled successfully by user {}", reservationId, userId);
        return true;
    }

}
