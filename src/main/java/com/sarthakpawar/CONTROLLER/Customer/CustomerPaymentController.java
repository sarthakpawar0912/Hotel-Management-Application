package com.sarthakpawar.CONTROLLER.Customer;

import com.sarthakpawar.DTO.PaymentDto;
import com.sarthakpawar.ENTITY.User;
import com.sarthakpawar.SERVICES.ADMIN.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/payments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CustomerPaymentController {

    private final PaymentService paymentService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }

    @PostMapping("/create-order/{reservationId}")
    public ResponseEntity<Map<String, Object>> createPaymentOrder(@PathVariable Long reservationId) {
        try {
            Map<String, Object> order = paymentService.createRazorpayOrderForReservation(reservationId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Payment order creation failed"));
        }
    }

    @PostMapping("/razorpay/create-order")
    public ResponseEntity<Map<String, Object>> createRazorpayOrder(
            @RequestParam Long reservationId,
            @RequestParam Long amount) {
        try {
            Map<String, Object> order = paymentService.createRazorpayOrder(reservationId, amount);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> paymentData) {
        try {
            String razorpayOrderId = paymentData.get("razorpayOrderId");
            String razorpayPaymentId = paymentData.get("razorpayPaymentId");
            String razorpaySignature = paymentData.get("razorpaySignature");

            PaymentDto payment = paymentService.verifyAndSavePayment(
                razorpayOrderId, razorpayPaymentId, razorpaySignature
            );
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<?> verifyRazorpayPayment(
            @RequestParam String razorpayOrderId,
            @RequestParam String razorpayPaymentId,
            @RequestParam String razorpaySignature) {
        try {
            PaymentDto payment = paymentService.verifyAndSavePayment(
                razorpayOrderId, razorpayPaymentId, razorpaySignature
            );
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-payments")
    public ResponseEntity<List<PaymentDto>> getMyPayments() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(paymentService.getPaymentsByReservation(reservationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
}
