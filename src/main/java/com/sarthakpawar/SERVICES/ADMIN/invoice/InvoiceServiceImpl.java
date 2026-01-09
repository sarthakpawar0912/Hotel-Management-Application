package com.sarthakpawar.SERVICES.ADMIN.invoice;

import com.sarthakpawar.DTO.InvoiceDto;
import com.sarthakpawar.ENTITY.Invoice;
import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.REPOSITORY.InvoiceRepository;
import com.sarthakpawar.REPOSITORY.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ReservationRepository reservationRepository;

    @Value("${gst.cgst:9}")
    private Double cgstRate;

    @Value("${gst.sgst:9}")
    private Double sgstRate;

    @Override
    @Transactional
    public InvoiceDto generateInvoice(Long reservationId) {
        // Check if invoice already exists
        if (invoiceRepository.findByReservationId(reservationId).isPresent()) {
            throw new RuntimeException("Invoice already exists for this reservation");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setReservation(reservation);
        invoice.setUser(reservation.getUser());

        // Set guest details
        invoice.setGuestName(reservation.getUser().getName());
        invoice.setGuestEmail(reservation.getUser().getEmail());

        // Calculate charges
        Long roomCharges = reservation.getPrice();
        invoice.setRoomCharges(roomCharges);
        invoice.setServiceCharges(0L);
        invoice.setAdditionalCharges(0L);
        invoice.setDiscount(0L);

        // Calculate subtotal
        Long subtotal = roomCharges;
        invoice.setSubtotal(subtotal);

        // Calculate taxes
        Long cgst = Math.round(subtotal * cgstRate / 100);
        Long sgst = Math.round(subtotal * sgstRate / 100);
        invoice.setCgst(cgst);
        invoice.setSgst(sgst);
        invoice.setTotalTax(cgst + sgst);

        // Calculate grand total
        Long grandTotal = subtotal + cgst + sgst;
        invoice.setGrandTotal(grandTotal);

        invoice.setAmountPaid(0L);
        invoice.setBalanceDue(grandTotal);
        invoice.setIsPaid(false);
        invoice.setDueDate(LocalDateTime.now().plusDays(7));

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return savedInvoice.getInvoiceDto();
    }

    @Override
    public InvoiceDto getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));
        return invoice.getInvoiceDto();
    }

    @Override
    public InvoiceDto getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));
        return invoice.getInvoiceDto();
    }

    @Override
    public InvoiceDto getInvoiceByReservation(Long reservationId) {
        Invoice invoice = invoiceRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found for reservation"));
        return invoice.getInvoiceDto();
    }

    @Override
    public List<InvoiceDto> getInvoicesByUser(Long userId) {
        return invoiceRepository.findByUserId(userId).stream()
                .map(Invoice::getInvoiceDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> getUnpaidInvoices() {
        return invoiceRepository.findByIsPaidFalse().stream()
                .map(Invoice::getInvoiceDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> getPaidInvoices() {
        return invoiceRepository.findByIsPaidTrue().stream()
                .map(Invoice::getInvoiceDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> getInvoicesWithOutstandingBalance() {
        return invoiceRepository.findWithOutstandingBalance().stream()
                .map(Invoice::getInvoiceDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(Invoice::getInvoiceDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InvoiceDto updateInvoice(Long id, InvoiceDto invoiceDto) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        invoice.setGuestName(invoiceDto.getGuestName());
        invoice.setGuestEmail(invoiceDto.getGuestEmail());
        invoice.setGuestPhone(invoiceDto.getGuestPhone());
        invoice.setGuestAddress(invoiceDto.getGuestAddress());
        invoice.setGstin(invoiceDto.getGstin());

        recalculateInvoice(invoice);

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return updatedInvoice.getInvoiceDto();
    }

    @Override
    @Transactional
    public InvoiceDto addAdditionalCharges(Long id, Long amount, String description) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        invoice.setAdditionalCharges(invoice.getAdditionalCharges() + amount);
        String existingDesc = invoice.getAdditionalChargesDescription();
        if (existingDesc != null && !existingDesc.isEmpty()) {
            invoice.setAdditionalChargesDescription(existingDesc + "; " + description);
        } else {
            invoice.setAdditionalChargesDescription(description);
        }

        recalculateInvoice(invoice);

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return updatedInvoice.getInvoiceDto();
    }

    @Override
    @Transactional
    public InvoiceDto applyDiscount(Long id, Long amount, String description) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        invoice.setDiscount(invoice.getDiscount() + amount);
        String existingDesc = invoice.getDiscountDescription();
        if (existingDesc != null && !existingDesc.isEmpty()) {
            invoice.setDiscountDescription(existingDesc + "; " + description);
        } else {
            invoice.setDiscountDescription(description);
        }

        recalculateInvoice(invoice);

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return updatedInvoice.getInvoiceDto();
    }

    @Override
    @Transactional
    public InvoiceDto recordPayment(Long id, Long amount) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        invoice.setAmountPaid(invoice.getAmountPaid() + amount);
        invoice.setBalanceDue(invoice.getGrandTotal() - invoice.getAmountPaid());

        if (invoice.getBalanceDue() <= 0) {
            invoice.setIsPaid(true);
            invoice.setPaidAt(LocalDateTime.now());
            invoice.setBalanceDue(0L);
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return updatedInvoice.getInvoiceDto();
    }

    @Override
    @Transactional
    public InvoiceDto markAsPaid(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        invoice.setIsPaid(true);
        invoice.setAmountPaid(invoice.getGrandTotal());
        invoice.setBalanceDue(0L);
        invoice.setPaidAt(LocalDateTime.now());

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return updatedInvoice.getInvoiceDto();
    }

    @Override
    @Transactional
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new EntityNotFoundException("Invoice not found");
        }
        invoiceRepository.deleteById(id);
    }

    @Override
    public String generateInvoiceNumber() {
        String lastNumber = invoiceRepository.getLastInvoiceNumber();
        String prefix = "INV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-";

        if (lastNumber == null || !lastNumber.contains(prefix.substring(0, 11))) {
            return prefix + "0001";
        }

        int lastSeq = Integer.parseInt(lastNumber.substring(lastNumber.lastIndexOf("-") + 1));
        return prefix + String.format("%04d", lastSeq + 1);
    }

    private void recalculateInvoice(Invoice invoice) {
        Long subtotal = invoice.getRoomCharges() + invoice.getServiceCharges() +
                       invoice.getAdditionalCharges() - invoice.getDiscount();
        invoice.setSubtotal(subtotal);

        Long cgst = Math.round(subtotal * cgstRate / 100);
        Long sgst = Math.round(subtotal * sgstRate / 100);
        invoice.setCgst(cgst);
        invoice.setSgst(sgst);
        invoice.setTotalTax(cgst + sgst);

        Long grandTotal = subtotal + cgst + sgst;
        invoice.setGrandTotal(grandTotal);
        invoice.setBalanceDue(grandTotal - invoice.getAmountPaid());

        if (invoice.getBalanceDue() <= 0) {
            invoice.setIsPaid(true);
            invoice.setBalanceDue(0L);
        } else {
            invoice.setIsPaid(false);
        }
    }
}
