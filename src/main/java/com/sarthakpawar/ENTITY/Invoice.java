package com.sarthakpawar.ENTITY;

import com.sarthakpawar.DTO.InvoiceDto;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Guest details (for invoice)
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String guestAddress;
    private String gstin;

    // Billing details
    private Long roomCharges;
    private Long serviceCharges = 0L;
    private Long additionalCharges = 0L;
    private String additionalChargesDescription;
    private Long discount = 0L;
    private String discountDescription;
    private Long subtotal;
    private Long cgst;
    private Long sgst;
    private Long totalTax;
    private Long grandTotal;

    // Payment status
    private Long amountPaid = 0L;
    private Long balanceDue;
    private Boolean isPaid = false;

    private LocalDateTime issuedAt;
    private LocalDateTime dueDate;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    @Column(length = 2000)
    private String notes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (issuedAt == null) {
            issuedAt = LocalDateTime.now();
        }
        if (dueDate == null) {
            dueDate = issuedAt.plusDays(7);
        }
    }

    public InvoiceDto getInvoiceDto() {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(id);
        dto.setInvoiceNumber(invoiceNumber);
        dto.setReservationId(reservation.getId());
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setUserName(user.getName());
        }
        dto.setGuestName(guestName);
        dto.setGuestEmail(guestEmail);
        dto.setGuestPhone(guestPhone);
        dto.setGuestAddress(guestAddress);
        dto.setGstin(gstin);
        dto.setRoomCharges(roomCharges);
        dto.setServiceCharges(serviceCharges);
        dto.setAdditionalCharges(additionalCharges);
        dto.setAdditionalChargesDescription(additionalChargesDescription);
        dto.setDiscount(discount);
        dto.setDiscountDescription(discountDescription);
        dto.setSubtotal(subtotal);
        dto.setCgst(cgst);
        dto.setSgst(sgst);
        dto.setTotalTax(totalTax);
        dto.setGrandTotal(grandTotal);
        dto.setAmountPaid(amountPaid);
        dto.setBalanceDue(balanceDue);
        dto.setIsPaid(isPaid);
        dto.setIssuedAt(issuedAt);
        dto.setDueDate(dueDate);
        dto.setPaidAt(paidAt);
        return dto;
    }
}
