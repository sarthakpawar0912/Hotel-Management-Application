package com.sarthakpawar.SERVICES.ADMIN.payment;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.sarthakpawar.DTO.PaymentDto;
import com.sarthakpawar.ENTITY.Payment;
import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.ENUMS.PaymentMethod;
import com.sarthakpawar.ENUMS.PaymentStatus;
import com.sarthakpawar.REPOSITORY.PaymentRepository;
import com.sarthakpawar.REPOSITORY.ReservationRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String razorpayKeySecret;

    @Value("${gst.cgst:9}")
    private Double cgstRate;

    @Value("${gst.sgst:9}")
    private Double sgstRate;

    private RazorpayClient razorpayClient;

    @PostConstruct
    public void init() {
        try {
            if (razorpayKeyId != null && razorpayKeySecret != null) {
                this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
                System.out.println("Razorpay client initialized successfully");
            } else {
                System.err.println("Razorpay credentials not configured");
            }
        } catch (RazorpayException e) {
            System.err.println("Failed to initialize Razorpay client: " + e.getMessage());
        }
    }

    private void ensureRazorpayClient() throws Exception {
        if (razorpayClient == null) {
            if (razorpayKeyId == null || razorpayKeySecret == null) {
                throw new RuntimeException("Razorpay credentials not configured");
            }
            razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> createRazorpayOrder(Long reservationId, Long amount) throws Exception {
        // Ensure Razorpay client is initialized
        ensureRazorpayClient();

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + reservationId));

        // Calculate taxes
        Long cgst = Math.round(amount * cgstRate / 100);
        Long sgst = Math.round(amount * sgstRate / 100);
        Long totalAmount = amount + cgst + sgst;

        System.out.println("Creating Razorpay order for amount: " + totalAmount + " (base: " + amount + ")");

        // Create Razorpay Order
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", totalAmount * 100); // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "rcpt_" + reservationId + "_" + System.currentTimeMillis());

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        // Create Payment record
        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setUser(reservation.getUser());
        payment.setAmount(amount);
        payment.setTaxAmount(cgst + sgst);
        payment.setCgst(cgst);
        payment.setSgst(sgst);
        payment.setTotalAmount(totalAmount);
        payment.setPaymentMethod(PaymentMethod.RAZORPAY);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setRazorpayOrderId(razorpayOrder.get("id"));
        payment.setReceiptNumber(generateReceiptNumber());

        paymentRepository.save(payment);

        // Return response
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", razorpayOrder.get("id"));
        response.put("amount", totalAmount * 100);
        response.put("currency", "INR");
        response.put("key", razorpayKeyId);
        response.put("paymentId", payment.getId());

        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> createRazorpayOrderForReservation(Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + reservationId));

        // Use totalAmount if set, otherwise use price (base room charge)
        Long amount = reservation.getTotalAmount() != null ? reservation.getTotalAmount() : reservation.getPrice();

        if (amount == null || amount <= 0) {
            throw new RuntimeException("Invalid reservation amount");
        }

        return createRazorpayOrder(reservationId, amount);
    }

    @Override
    @Transactional
    public PaymentDto verifyAndSavePayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws Exception {
        // Verify signature
        String generatedSignature = generateSignature(razorpayOrderId, razorpayPaymentId);

        if (!generatedSignature.equals(razorpaySignature)) {
            throw new RuntimeException("Payment verification failed - Invalid signature");
        }

        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for order: " + razorpayOrderId));

        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setRazorpaySignature(razorpaySignature);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        payment.setTransactionId(generateTransactionId());

        Payment savedPayment = paymentRepository.save(payment);
        return savedPayment.getPaymentDto();
    }

    private String generateSignature(String orderId, String paymentId) throws Exception {
        String data = orderId + "|" + paymentId;
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(razorpayKeySecret.getBytes(), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hash = sha256Hmac.doFinal(data.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    @Transactional
    public PaymentDto createPayment(PaymentDto paymentDto) {
        Payment payment = new Payment();
        mapDtoToEntity(paymentDto, payment);

        if (paymentDto.getReservationId() != null) {
            Reservation reservation = reservationRepository.findById(paymentDto.getReservationId())
                    .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
            payment.setReservation(reservation);
            payment.setUser(reservation.getUser());
        }

        // Calculate taxes if not provided
        if (payment.getCgst() == null && payment.getAmount() != null) {
            Long cgst = Math.round(payment.getAmount() * cgstRate / 100);
            Long sgst = Math.round(payment.getAmount() * sgstRate / 100);
            payment.setCgst(cgst);
            payment.setSgst(sgst);
            payment.setTaxAmount(cgst + sgst);
            payment.setTotalAmount(payment.getAmount() + payment.getTaxAmount());
        }

        payment.setReceiptNumber(generateReceiptNumber());
        payment.setTransactionId(generateTransactionId());

        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            payment.setPaidAt(LocalDateTime.now());
        }

        Payment savedPayment = paymentRepository.save(payment);
        return savedPayment.getPaymentDto();
    }

    @Override
    @Transactional
    public PaymentDto updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId));

        payment.setPaymentStatus(status);
        if (status == PaymentStatus.COMPLETED) {
            payment.setPaidAt(LocalDateTime.now());
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return updatedPayment.getPaymentDto();
    }

    @Override
    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
        return payment.getPaymentDto();
    }

    @Override
    public PaymentDto getPaymentByRazorpayOrderId(String razorpayOrderId) {
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for order: " + razorpayOrderId));
        return payment.getPaymentDto();
    }

    @Override
    public PaymentDto getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for transaction: " + transactionId));
        return payment.getPaymentDto();
    }

    @Override
    public List<PaymentDto> getPaymentsByReservation(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId).stream()
                .map(Payment::getPaymentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(Payment::getPaymentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status).stream()
                .map(Payment::getPaymentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByDateRange(startDate, endDate).stream()
                .map(Payment::getPaymentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(Payment::getPaymentDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long getTotalRevenue() {
        Long revenue = paymentRepository.getTotalRevenue();
        return revenue != null ? revenue : 0L;
    }

    @Override
    public Long getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Long revenue = paymentRepository.getRevenueByDateRange(startDate, endDate);
        return revenue != null ? revenue : 0L;
    }

    @Override
    public Map<String, Long> getTaxCollectionByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Long> taxes = new HashMap<>();
        Long cgst = paymentRepository.getTotalCgstByDateRange(startDate, endDate);
        Long sgst = paymentRepository.getTotalSgstByDateRange(startDate, endDate);

        taxes.put("cgst", cgst != null ? cgst : 0L);
        taxes.put("sgst", sgst != null ? sgst : 0L);
        taxes.put("total", (cgst != null ? cgst : 0L) + (sgst != null ? sgst : 0L));

        return taxes;
    }

    @Override
    public Long getPaymentCountByStatus(PaymentStatus status) {
        return paymentRepository.countByStatus(status);
    }

    private String generateReceiptNumber() {
        return "RCPT-" + System.currentTimeMillis();
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void mapDtoToEntity(PaymentDto dto, Payment entity) {
        entity.setAmount(dto.getAmount());
        entity.setTaxAmount(dto.getTaxAmount());
        entity.setCgst(dto.getCgst());
        entity.setSgst(dto.getSgst());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setPaymentStatus(dto.getPaymentStatus() != null ? dto.getPaymentStatus() : PaymentStatus.PENDING);
        entity.setNotes(dto.getNotes());
    }
}
