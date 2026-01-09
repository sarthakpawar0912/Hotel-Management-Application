package com.sarthakpawar.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InvoiceDto {

    private Long id;
    private String invoiceNumber;
    private Long reservationId;
    private Long userId;
    private String userName;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String guestAddress;
    private String gstin;
    private Long roomCharges;
    private Long serviceCharges;
    private Long additionalCharges;
    private String additionalChargesDescription;
    private Long discount;
    private String discountDescription;
    private Long subtotal;
    private Long cgst;
    private Long sgst;
    private Long totalTax;
    private Long grandTotal;
    private Long amountPaid;
    private Long balanceDue;
    private Boolean isPaid;
    private LocalDateTime issuedAt;
    private LocalDateTime dueDate;
    private LocalDateTime paidAt;
}
