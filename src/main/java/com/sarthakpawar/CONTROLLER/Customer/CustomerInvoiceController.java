package com.sarthakpawar.CONTROLLER.Customer;

import com.sarthakpawar.DTO.InvoiceDto;
import com.sarthakpawar.ENTITY.User;
import com.sarthakpawar.SERVICES.ADMIN.invoice.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/invoices")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CustomerInvoiceController {

    private final InvoiceService invoiceService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }

    @GetMapping("/my-invoices")
    public ResponseEntity<List<InvoiceDto>> getMyInvoices() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(invoiceService.getInvoicesByUser(userId));
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
}
