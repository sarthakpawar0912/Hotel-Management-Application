package com.sarthakpawar.CONTROLLER.ADMIN;

import com.sarthakpawar.DTO.InvoiceDto;
import com.sarthakpawar.SERVICES.ADMIN.invoice.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/invoices")
@RequiredArgsConstructor
@CrossOrigin("*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/generate/{reservationId}")
    public ResponseEntity<InvoiceDto> generateInvoice(@PathVariable Long reservationId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.generateInvoice(reservationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceDto> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(invoiceService.getInvoiceByNumber(invoiceNumber));
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<InvoiceDto> getInvoiceByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(invoiceService.getInvoiceByReservation(reservationId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvoiceDto>> getInvoicesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDto>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<InvoiceDto>> getUnpaidInvoices() {
        return ResponseEntity.ok(invoiceService.getUnpaidInvoices());
    }

    @GetMapping("/paid")
    public ResponseEntity<List<InvoiceDto>> getPaidInvoices() {
        return ResponseEntity.ok(invoiceService.getPaidInvoices());
    }

    @GetMapping("/outstanding")
    public ResponseEntity<List<InvoiceDto>> getInvoicesWithOutstandingBalance() {
        return ResponseEntity.ok(invoiceService.getInvoicesWithOutstandingBalance());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDto> updateInvoice(@PathVariable Long id, @RequestBody InvoiceDto invoiceDto) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, invoiceDto));
    }

    @PostMapping("/{id}/additional-charges")
    public ResponseEntity<InvoiceDto> addAdditionalCharges(
            @PathVariable Long id,
            @RequestParam Long amount,
            @RequestParam String description) {
        return ResponseEntity.ok(invoiceService.addAdditionalCharges(id, amount, description));
    }

    @PostMapping("/{id}/discount")
    public ResponseEntity<InvoiceDto> applyDiscount(
            @PathVariable Long id,
            @RequestParam Long amount,
            @RequestParam String description) {
        return ResponseEntity.ok(invoiceService.applyDiscount(id, amount, description));
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<InvoiceDto> recordPayment(
            @PathVariable Long id,
            @RequestParam Long amount) {
        return ResponseEntity.ok(invoiceService.recordPayment(id, amount));
    }

    @PostMapping("/{id}/mark-paid")
    public ResponseEntity<InvoiceDto> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.markAsPaid(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
