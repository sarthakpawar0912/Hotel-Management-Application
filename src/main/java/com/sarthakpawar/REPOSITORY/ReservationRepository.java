package com.sarthakpawar.REPOSITORY;

import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.ENUMS.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findAllByUserId(Pageable pageable, Long userId);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByRoomId(Long roomId);

    List<Reservation> findByReservationStatus(ReservationStatus status);

    List<Reservation> findByUserIdAndReservationStatus(Long userId, ReservationStatus status);

    List<Reservation> findByUserIdAndRoomIdAndReservationStatus(Long userId, Long roomId, ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId AND r.reservationStatus NOT IN ('CANCELLED', 'CHECKED_OUT') " +
           "AND ((r.checkInDate <= :checkOut AND r.checkOutDate >= :checkIn))")
    List<Reservation> findConflictingReservations(@Param("roomId") Long roomId,
                                                   @Param("checkIn") LocalDate checkIn,
                                                   @Param("checkOut") LocalDate checkOut);

    @Query("SELECT r FROM Reservation r WHERE r.checkInDate = :date AND r.reservationStatus = 'APPROVED'")
    List<Reservation> findTodayCheckIns(@Param("date") LocalDate date);

    @Query("SELECT r FROM Reservation r WHERE r.checkOutDate = :date AND r.reservationStatus = 'CHECKED_IN'")
    List<Reservation> findTodayCheckOuts(@Param("date") LocalDate date);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.reservationStatus = :status")
    Long countByStatus(@Param("status") ReservationStatus status);

    @Query("SELECT SUM(r.totalAmount) FROM Reservation r WHERE r.reservationStatus = 'CHECKED_OUT' AND r.checkOutDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.createdAt >= :startDate")
    Long countReservationsAfter(@Param("startDate") java.time.LocalDateTime startDate);

    List<Reservation> findByCheckInDateBetween(LocalDate startDate, LocalDate endDate);

    List<Reservation> findByCheckOutDateBetween(LocalDate startDate, LocalDate endDate);
}

