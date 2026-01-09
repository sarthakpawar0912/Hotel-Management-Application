package com.sarthakpawar.DTO;

import com.sarthakpawar.ENUMS.PaymentMethod;
import com.sarthakpawar.ENUMS.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDto {

    private Long id;
    private Long reservationId;
    private Long userId;
    private String userName;
    private Long amount;
    private Long taxAmount;
    private Long cgst;
    private Long sgst;
    private Long totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private String transactionId;
    private String receiptNumber;
    private String notes;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
