package com.sarthakpawar.REPOSITORY;

import com.sarthakpawar.ENTITY.Payment;
import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.ENUMS.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByReservation(Reservation reservation);

    List<Payment> findByReservationId(Long reservationId);

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByPaymentStatus(PaymentStatus status);

    List<Payment> findByUserId(Long userId);

    @Query("SELECT p FROM Payment p WHERE p.paidAt BETWEEN :startDate AND :endDate")
    List<Payment> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(p.totalAmount) FROM Payment p WHERE p.paymentStatus = 'COMPLETED'")
    Long getTotalRevenue();

    @Query("SELECT SUM(p.totalAmount) FROM Payment p WHERE p.paymentStatus = 'COMPLETED' AND p.paidAt BETWEEN :startDate AND :endDate")
    Long getRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(p.cgst) FROM Payment p WHERE p.paymentStatus = 'COMPLETED' AND p.paidAt BETWEEN :startDate AND :endDate")
    Long getTotalCgstByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(p.sgst) FROM Payment p WHERE p.paymentStatus = 'COMPLETED' AND p.paidAt BETWEEN :startDate AND :endDate")
    Long getTotalSgstByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentStatus = :status")
    Long countByStatus(@Param("status") PaymentStatus status);

    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.reservation.id = :reservationId AND p.paymentStatus = 'COMPLETED'")
    boolean existsCompletedPaymentByReservationId(@Param("reservationId") Long reservationId);

    Optional<Payment> findByReservationIdAndPaymentStatus(Long reservationId, PaymentStatus status);
}
