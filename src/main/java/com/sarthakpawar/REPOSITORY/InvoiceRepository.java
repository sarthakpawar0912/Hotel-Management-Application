package com.sarthakpawar.REPOSITORY;

import com.sarthakpawar.ENTITY.Invoice;
import com.sarthakpawar.ENTITY.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByReservation(Reservation reservation);

    Optional<Invoice> findByReservationId(Long reservationId);

    List<Invoice> findByUserId(Long userId);

    List<Invoice> findByIsPaidFalse();

    List<Invoice> findByIsPaidTrue();

    @Query("SELECT i FROM Invoice i WHERE i.issuedAt BETWEEN :startDate AND :endDate")
    List<Invoice> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT i FROM Invoice i WHERE i.balanceDue > 0")
    List<Invoice> findWithOutstandingBalance();

    @Query("SELECT SUM(i.grandTotal) FROM Invoice i WHERE i.issuedAt BETWEEN :startDate AND :endDate")
    Long getTotalInvoicedAmount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT MAX(i.invoiceNumber) FROM Invoice i")
    String getLastInvoiceNumber();
}
