package com.sarthakpawar.SERVICES.ADMIN.payment;

import com.sarthakpawar.DTO.PaymentDto;
import com.sarthakpawar.ENUMS.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PaymentService {

    // Razorpay Integration
    Map<String, Object> createRazorpayOrder(Long reservationId, Long amount) throws Exception;

    Map<String, Object> createRazorpayOrderForReservation(Long reservationId) throws Exception;

    PaymentDto verifyAndSavePayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws Exception;

    // Payment Operations
    PaymentDto createPayment(PaymentDto paymentDto);

    PaymentDto updatePaymentStatus(Long paymentId, PaymentStatus status);

    PaymentDto getPaymentById(Long id);

    PaymentDto getPaymentByRazorpayOrderId(String razorpayOrderId);

    PaymentDto getPaymentByTransactionId(String transactionId);

    List<PaymentDto> getPaymentsByReservation(Long reservationId);

    List<PaymentDto> getPaymentsByUser(Long userId);

    List<PaymentDto> getPaymentsByStatus(PaymentStatus status);

    List<PaymentDto> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<PaymentDto> getAllPayments();

    // Revenue & Reports
    Long getTotalRevenue();

    Long getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Map<String, Long> getTaxCollectionByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Long getPaymentCountByStatus(PaymentStatus status);
}
