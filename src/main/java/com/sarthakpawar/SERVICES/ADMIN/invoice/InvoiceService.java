package com.sarthakpawar.SERVICES.ADMIN.invoice;

import com.sarthakpawar.DTO.InvoiceDto;

import java.util.List;

public interface InvoiceService {

    InvoiceDto generateInvoice(Long reservationId);

    InvoiceDto getInvoiceById(Long id);

    InvoiceDto getInvoiceByNumber(String invoiceNumber);

    InvoiceDto getInvoiceByReservation(Long reservationId);

    List<InvoiceDto> getInvoicesByUser(Long userId);

    List<InvoiceDto> getUnpaidInvoices();

    List<InvoiceDto> getPaidInvoices();

    List<InvoiceDto> getInvoicesWithOutstandingBalance();

    List<InvoiceDto> getAllInvoices();

    InvoiceDto updateInvoice(Long id, InvoiceDto invoiceDto);

    InvoiceDto addAdditionalCharges(Long id, Long amount, String description);

    InvoiceDto applyDiscount(Long id, Long amount, String description);

    InvoiceDto recordPayment(Long id, Long amount);

    InvoiceDto markAsPaid(Long id);

    void deleteInvoice(Long id);

    String generateInvoiceNumber();
}
