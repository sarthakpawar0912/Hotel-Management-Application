package com.sarthakpawar.CONTROLLER.ADMIN;

import com.sarthakpawar.DTO.PaymentDto;
import com.sarthakpawar.ENUMS.PaymentStatus;
import com.sarthakpawar.SERVICES.ADMIN.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/razorpay/create-order")
    public ResponseEntity<Map<String, Object>> createRazorpayOrder(
            @RequestParam Long reservationId,
            @RequestParam Long amount) {
        try {
            Map<String, Object> order = paymentService.createRazorpayOrder(reservationId, amount);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestParam String razorpayOrderId,
            @RequestParam String razorpayPaymentId,
            @RequestParam String razorpaySignature) {
        try {
            PaymentDto payment = paymentService.verifyAndSavePayment(razorpayOrderId, razorpayPaymentId, razorpaySignature);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@RequestBody PaymentDto paymentDto) {
        PaymentDto createdPayment = paymentService.createPayment(paymentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentDto> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) {
        PaymentDto updatedPayment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(updatedPayment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        PaymentDto payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDto> getPaymentByOrderId(@PathVariable String orderId) {
        PaymentDto payment = paymentService.getPaymentByRazorpayOrderId(orderId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<PaymentDto> getPaymentByTransactionId(@PathVariable String transactionId) {
        PaymentDto payment = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByReservation(@PathVariable Long reservationId) {
        List<PaymentDto> payments = paymentService.getPaymentsByReservation(reservationId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByUser(@PathVariable Long userId) {
        List<PaymentDto> payments = paymentService.getPaymentsByUser(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        List<PaymentDto> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        List<PaymentDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/revenue/total")
    public ResponseEntity<Map<String, Long>> getTotalRevenue() {
        Long revenue = paymentService.getTotalRevenue();
        return ResponseEntity.ok(Map.of("totalRevenue", revenue));
    }

    @GetMapping("/revenue/range")
    public ResponseEntity<Map<String, Long>> getRevenueByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        Long revenue = paymentService.getRevenueByDateRange(start, end);
        return ResponseEntity.ok(Map.of("revenue", revenue));
    }

    @GetMapping("/taxes/range")
    public ResponseEntity<Map<String, Long>> getTaxesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        Map<String, Long> taxes = paymentService.getTaxCollectionByDateRange(start, end);
        return ResponseEntity.ok(taxes);
    }
}
