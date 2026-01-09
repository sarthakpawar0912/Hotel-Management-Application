package com.sarthakpawar.ENTITY;

import com.sarthakpawar.DTO.PaymentDto;
import com.sarthakpawar.ENUMS.PaymentMethod;
import com.sarthakpawar.ENUMS.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long amount;
    private Long taxAmount;
    private Long cgst;
    private Long sgst;
    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private String transactionId;
    private String receiptNumber;

    @Column(length = 1000)
    private String notes;

    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public PaymentDto getPaymentDto() {
        PaymentDto dto = new PaymentDto();
        dto.setId(id);
        dto.setReservationId(reservation.getId());
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setUserName(user.getName());
        }
        dto.setAmount(amount);
        dto.setTaxAmount(taxAmount);
        dto.setCgst(cgst);
        dto.setSgst(sgst);
        dto.setTotalAmount(totalAmount);
        dto.setPaymentMethod(paymentMethod);
        dto.setPaymentStatus(paymentStatus);
        dto.setRazorpayOrderId(razorpayOrderId);
        dto.setRazorpayPaymentId(razorpayPaymentId);
        dto.setRazorpaySignature(razorpaySignature);
        dto.setTransactionId(transactionId);
        dto.setReceiptNumber(receiptNumber);
        dto.setNotes(notes);
        dto.setPaidAt(paidAt);
        dto.setCreatedAt(createdAt);
        return dto;
    }
}
