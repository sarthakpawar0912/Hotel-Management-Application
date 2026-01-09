package com.sarthakpawar.SERVICES.notification;

import com.sarthakpawar.ENTITY.Invoice;
import com.sarthakpawar.ENTITY.Payment;
import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.ENTITY.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.application.name:Hotel Management}")
    private String hotelName;

    // Common email styles - Purple/Violet theme matching Angular UI
    private static final String EMAIL_STYLES = """
        <style>
            @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

            * { margin: 0; padding: 0; box-sizing: border-box; }

            body {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                line-height: 1.6;
                color: #333;
                background-color: #f5f5f5;
            }

            .email-wrapper {
                max-width: 600px;
                margin: 0 auto;
                background: #ffffff;
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            }

            .email-header {
                background: linear-gradient(135deg, #6a1b9a, #ab47bc);
                color: white;
                padding: 32px 24px;
                text-align: center;
            }

            .email-header.success {
                background: linear-gradient(135deg, #43a047, #66bb6a);
            }

            .email-header.warning {
                background: linear-gradient(135deg, #fb8c00, #ffa726);
            }

            .email-header.danger {
                background: linear-gradient(135deg, #e53935, #ef5350);
            }

            .email-header.info {
                background: linear-gradient(135deg, #1e88e5, #42a5f5);
            }

            .hotel-logo {
                font-size: 28px;
                font-weight: 700;
                margin-bottom: 8px;
                letter-spacing: -0.5px;
            }

            .email-title {
                font-size: 24px;
                font-weight: 600;
                margin: 0;
            }

            .email-subtitle {
                font-size: 14px;
                opacity: 0.9;
                margin-top: 8px;
            }

            .email-body {
                padding: 32px 24px;
            }

            .greeting {
                font-size: 16px;
                color: #333;
                margin-bottom: 16px;
            }

            .message {
                font-size: 15px;
                color: #555;
                margin-bottom: 24px;
            }

            .details-card {
                background: linear-gradient(135deg, #faf5ff, #f3e5f5);
                border-radius: 12px;
                padding: 24px;
                margin: 24px 0;
                border-left: 4px solid #6a1b9a;
            }

            .details-card.success {
                background: linear-gradient(135deg, #e8f5e9, #c8e6c9);
                border-left-color: #43a047;
            }

            .details-card.danger {
                background: linear-gradient(135deg, #ffebee, #ffcdd2);
                border-left-color: #e53935;
            }

            .details-card h3 {
                font-size: 14px;
                font-weight: 600;
                color: #6a1b9a;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                margin-bottom: 16px;
            }

            .detail-row {
                display: flex;
                justify-content: space-between;
                padding: 10px 0;
                border-bottom: 1px solid rgba(106, 27, 154, 0.1);
            }

            .detail-row:last-child {
                border-bottom: none;
            }

            .detail-label {
                font-size: 14px;
                color: #666;
            }

            .detail-value {
                font-size: 14px;
                font-weight: 600;
                color: #333;
            }

            .total-row {
                background: rgba(106, 27, 154, 0.1);
                padding: 12px;
                border-radius: 8px;
                margin-top: 12px;
            }

            .total-row .detail-value {
                font-size: 18px;
                color: #6a1b9a;
            }

            .cta-button {
                display: inline-block;
                background: linear-gradient(135deg, #6a1b9a, #ab47bc);
                color: white !important;
                text-decoration: none;
                padding: 14px 32px;
                border-radius: 8px;
                font-weight: 600;
                font-size: 14px;
                text-align: center;
                margin: 24px 0;
            }

            .note-box {
                background: #fff8e1;
                border-radius: 8px;
                padding: 16px;
                margin: 24px 0;
                border-left: 4px solid #ffc107;
            }

            .note-box p {
                font-size: 13px;
                color: #856404;
                margin: 0;
            }

            .email-footer {
                background: #f8f9fa;
                padding: 24px;
                text-align: center;
                border-top: 1px solid #eee;
            }

            .footer-brand {
                font-size: 16px;
                font-weight: 600;
                color: #6a1b9a;
                margin-bottom: 8px;
            }

            .footer-contact {
                font-size: 13px;
                color: #888;
                margin-bottom: 16px;
            }

            .footer-legal {
                font-size: 11px;
                color: #aaa;
            }

            .divider {
                height: 1px;
                background: linear-gradient(90deg, transparent, #ddd, transparent);
                margin: 24px 0;
            }

            .status-badge {
                display: inline-block;
                padding: 6px 12px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .status-badge.confirmed {
                background: #e8f5e9;
                color: #2e7d32;
            }

            .status-badge.cancelled {
                background: #ffebee;
                color: #c62828;
            }

            .status-badge.pending {
                background: #fff8e1;
                color: #f57f17;
            }
        </style>
        """;

    @Override
    @Async
    public void sendBookingConfirmation(Reservation reservation) {
        try {
            Context context = new Context();
            context.setVariable("guestName", reservation.getUser().getName());
            context.setVariable("reservationId", reservation.getId());
            context.setVariable("roomName", reservation.getRoom().getName());
            context.setVariable("roomType", reservation.getRoom().getType());
            context.setVariable("checkInDate", reservation.getCheckInDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            context.setVariable("checkOutDate", reservation.getCheckOutDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            context.setVariable("totalPrice", reservation.getPrice());
            context.setVariable("hotelName", hotelName);

            String subject = "Booking Confirmed! - " + hotelName + " #" + reservation.getId();
            String body = buildBookingConfirmationEmail(reservation);

            sendHtmlEmail(reservation.getUser().getEmail(), subject, body);
            log.info("Booking confirmation email sent to: {}", reservation.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send booking confirmation email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendBookingCancellation(Reservation reservation) {
        try {
            String subject = "Booking Cancelled - " + hotelName + " #" + reservation.getId();
            String body = buildBookingCancellationEmail(reservation);

            sendHtmlEmail(reservation.getUser().getEmail(), subject, body);
            log.info("Booking cancellation email sent to: {}", reservation.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send booking cancellation email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendCheckInReminder(Reservation reservation) {
        try {
            String subject = "Check-in Tomorrow! - " + hotelName;
            String body = buildCheckInReminderEmail(reservation);

            sendHtmlEmail(reservation.getUser().getEmail(), subject, body);
            log.info("Check-in reminder email sent to: {}", reservation.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send check-in reminder email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendCheckOutReminder(Reservation reservation) {
        try {
            String subject = "Check-out Reminder - " + hotelName;
            String body = buildCheckOutReminderEmail(reservation);

            sendHtmlEmail(reservation.getUser().getEmail(), subject, body);
            log.info("Check-out reminder email sent to: {}", reservation.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send check-out reminder email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPaymentConfirmation(Payment payment) {
        try {
            String subject = "Payment Successful - " + hotelName;
            String body = buildPaymentConfirmationEmail(payment);

            sendHtmlEmail(payment.getUser().getEmail(), subject, body);
            log.info("Payment confirmation email sent to: {}", payment.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send payment confirmation email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPaymentFailure(Payment payment) {
        try {
            String subject = "Payment Failed - " + hotelName;
            String body = buildPaymentFailureEmail(payment);

            sendHtmlEmail(payment.getUser().getEmail(), subject, body);
            log.info("Payment failure email sent to: {}", payment.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send payment failure email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendInvoice(Invoice invoice) {
        try {
            String subject = "Invoice #" + invoice.getInvoiceNumber() + " - " + hotelName;
            String body = buildInvoiceEmail(invoice);

            sendHtmlEmail(invoice.getUser().getEmail(), subject, body);
            log.info("Invoice email sent to: {}", invoice.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send invoice email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(User user) {
        try {
            String subject = "Welcome to " + hotelName + "!";
            String body = buildWelcomeEmail(user);

            sendHtmlEmail(user.getEmail(), subject, body);
            log.info("Welcome email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPasswordReset(User user, String resetToken) {
        try {
            String subject = "Password Reset Request - " + hotelName;
            String body = buildPasswordResetEmail(user, resetToken);

            sendHtmlEmail(user.getEmail(), subject, body);
            log.info("Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendReviewRequest(Reservation reservation) {
        try {
            String subject = "How was your stay? - " + hotelName;
            String body = buildReviewRequestEmail(reservation);

            sendHtmlEmail(reservation.getUser().getEmail(), subject, body);
            log.info("Review request email sent to: {}", reservation.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send review request email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPromotionalEmail(User user, String subject, String content) {
        try {
            String body = buildPromotionalEmail(user, content);
            sendHtmlEmail(user.getEmail(), subject, body);
            log.info("Promotional email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send promotional email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendGenericEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Generic email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send generic email: {}", e.getMessage());
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String getEmailWrapper(String headerClass, String title, String subtitle, String content) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                %s
            </head>
            <body style="margin: 0; padding: 20px; background-color: #f5f5f5;">
                <div class="email-wrapper">
                    <div class="email-header %s">
                        <div class="hotel-logo">%s</div>
                        <h1 class="email-title">%s</h1>
                        %s
                    </div>
                    <div class="email-body">
                        %s
                    </div>
                    <div class="email-footer">
                        <div class="footer-brand">%s</div>
                        <div class="footer-contact">
                            Questions? Contact us at %s
                        </div>
                        <div class="footer-legal">
                            This is an automated message. Please do not reply directly to this email.
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                EMAIL_STYLES,
                headerClass,
                hotelName,
                title,
                subtitle != null ? "<p class=\"email-subtitle\">" + subtitle + "</p>" : "",
                content,
                hotelName,
                fromEmail
            );
    }

    private String buildBookingConfirmationEmail(Reservation reservation) {
        String content = """
            <p class="greeting">Dear %s,</p>
            <p class="message">Great news! Your booking has been confirmed. We're excited to host you!</p>

            <div class="details-card success">
                <h3>Reservation Details</h3>
                <table style="width: 100%%; border-collapse: collapse;">
                    <tr class="detail-row">
                        <td class="detail-label">Confirmation #</td>
                        <td class="detail-value" style="text-align: right;">#%d</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Room</td>
                        <td class="detail-value" style="text-align: right;">%s (%s)</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Check-in</td>
                        <td class="detail-value" style="text-align: right;">%s</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Check-out</td>
                        <td class="detail-value" style="text-align: right;">%s</td>
                    </tr>
                </table>
                <div class="total-row">
                    <table style="width: 100%%;">
                        <tr>
                            <td class="detail-label"><strong>Total Amount</strong></td>
                            <td class="detail-value" style="text-align: right; color: #2e7d32; font-size: 18px;">₹%,d</td>
                        </tr>
                    </table>
                </div>
            </div>

            <div class="note-box">
                <p><strong>Important:</strong> Please bring a valid ID proof for verification during check-in. Check-in time starts at 2:00 PM.</p>
            </div>

            <p class="message">We look forward to welcoming you!</p>
            """.formatted(
                reservation.getUser().getName(),
                reservation.getId(),
                reservation.getRoom().getName(),
                reservation.getRoom().getType(),
                reservation.getCheckInDate().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")),
                reservation.getCheckOutDate().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")),
                reservation.getPrice()
            );

        return getEmailWrapper("success", "Booking Confirmed!", "Your reservation is all set", content);
    }

    private String buildBookingCancellationEmail(Reservation reservation) {
        String content = """
            <p class="greeting">Dear %s,</p>
            <p class="message">Your booking has been cancelled as requested.</p>

            <div class="details-card danger">
                <h3>Cancelled Reservation</h3>
                <table style="width: 100%%; border-collapse: collapse;">
                    <tr class="detail-row">
                        <td class="detail-label">Reservation #</td>
                        <td class="detail-value" style="text-align: right;">#%d</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Room</td>
                        <td class="detail-value" style="text-align: right;">%s</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Original Check-in</td>
                        <td class="detail-value" style="text-align: right;">%s</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Status</td>
                        <td class="detail-value" style="text-align: right;"><span class="status-badge cancelled">Cancelled</span></td>
                    </tr>
                </table>
            </div>

            <div class="note-box">
                <p><strong>Refund Policy:</strong> As per our cancellation policy, please note that refunds are subject to our terms and conditions. If you have any questions, please contact our support team.</p>
            </div>

            <p class="message">We hope to serve you in the future. Feel free to make a new reservation anytime!</p>
            """.formatted(
                reservation.getUser().getName(),
                reservation.getId(),
                reservation.getRoom().getName(),
                reservation.getCheckInDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            );

        return getEmailWrapper("danger", "Booking Cancelled", "Reservation #" + reservation.getId(), content);
    }

    private String buildCheckInReminderEmail(Reservation reservation) {
        String content = """
            <p class="greeting">Dear %s,</p>
            <p class="message">This is a friendly reminder that your check-in is coming up soon!</p>

            <div class="details-card">
                <h3>Your Upcoming Stay</h3>
                <table style="width: 100%%; border-collapse: collapse;">
                    <tr class="detail-row">
                        <td class="detail-label">Reservation #</td>
                        <td class="detail-value" style="text-align: right;">#%d</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Room</td>
                        <td class="detail-value" style="text-align: right;">%s</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Check-in Date</td>
                        <td class="detail-value" style="text-align: right;">%s</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Check-in Time</td>
                        <td class="detail-value" style="text-align: right;">2:00 PM onwards</td>
                    </tr>
                </table>
            </div>

            <div class="note-box">
                <p><strong>Remember to bring:</strong> Valid ID proof (Aadhaar/Passport/Driving License) for verification at check-in.</p>
            </div>

            <p class="message">We're looking forward to hosting you!</p>
            """.formatted(
                reservation.getUser().getName(),
                reservation.getId(),
                reservation.getRoom().getName(),
                reservation.getCheckInDate().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"))
            );

        return getEmailWrapper("info", "Check-in Reminder", "Your stay begins soon!", content);
    }

    private String buildCheckOutReminderEmail(Reservation reservation) {
        String content = """
            <p class="greeting">Dear %s,</p>
            <p class="message">We hope you're enjoying your stay! This is a friendly reminder that your check-out is scheduled for today.</p>

            <div class="details-card">
                <h3>Check-out Details</h3>
                <table style="width: 100%%; border-collapse: collapse;">
                    <tr class="detail-row">
                        <td class="detail-label">Reservation #</td>
                        <td class="detail-value" style="text-align: right;">#%d</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Room</td>
                        <td class="detail-value" style="text-align: right;">%s</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Check-out Date</td>
                        <td class="detail-value" style="text-align: right;">%s</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Check-out Time</td>
                        <td class="detail-value" style="text-align: right;">Before 11:00 AM</td>
                    </tr>
                </table>
            </div>

            <div class="note-box">
                <p><strong>Before you leave:</strong> Please ensure you've collected all your belongings. Don't forget to settle any outstanding charges at the reception.</p>
            </div>

            <p class="message">Thank you for staying with us. We hope to see you again soon!</p>
            """.formatted(
                reservation.getUser().getName(),
                reservation.getId(),
                reservation.getRoom().getName(),
                reservation.getCheckOutDate().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"))
            );

        return getEmailWrapper("warning", "Check-out Reminder", "Time to say goodbye (for now!)", content);
    }

    private String buildPaymentConfirmationEmail(Payment payment) {
        String content = """
            <p class="greeting">Dear %s,</p>
            <p class="message">Your payment has been successfully processed. Thank you!</p>

            <div class="details-card success">
                <h3>Payment Receipt</h3>
                <table style="width: 100%%; border-collapse: collapse;">
                    <tr class="detail-row">
                        <td class="detail-label">Transaction ID</td>
                        <td class="detail-value" style="text-align: right;">%s</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Receipt Number</td>
                        <td class="detail-value" style="text-align: right;">%s</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Base Amount</td>
                        <td class="detail-value" style="text-align: right;">₹%,d</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">CGST (9%%)</td>
                        <td class="detail-value" style="text-align: right;">₹%,d</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">SGST (9%%)</td>
                        <td class="detail-value" style="text-align: right;">₹%,d</td>
                    </tr>
                </table>
                <div class="total-row">
                    <table style="width: 100%%;">
                        <tr>
                            <td class="detail-label"><strong>Total Paid</strong></td>
                            <td class="detail-value" style="text-align: right; color: #2e7d32; font-size: 20px;">₹%,d</td>
                        </tr>
                    </table>
                </div>
            </div>

            <div class="note-box">
                <p><strong>Note:</strong> This serves as your official payment receipt. Please keep it for your records.</p>
            </div>

            <p class="message">Your booking is now fully paid. We look forward to hosting you!</p>
            """.formatted(
                payment.getUser().getName(),
                payment.getTransactionId(),
                payment.getReceiptNumber(),
                payment.getAmount(),
                payment.getCgst(),
                payment.getSgst(),
                payment.getTotalAmount()
            );

        return getEmailWrapper("success", "Payment Successful", "Thank you for your payment!", content);
    }

    private String buildPaymentFailureEmail(Payment payment) {
        String content = """
            <p class="greeting">Dear %s,</p>
            <p class="message">Unfortunately, we were unable to process your payment. Please try again.</p>

            <div class="details-card danger">
                <h3>Payment Details</h3>
                <table style="width: 100%%; border-collapse: collapse;">
                    <tr class="detail-row">
                        <td class="detail-label">Amount</td>
                        <td class="detail-value" style="text-align: right;">₹%,d</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Status</td>
                        <td class="detail-value" style="text-align: right;"><span class="status-badge cancelled">Failed</span></td>
                    </tr>
                </table>
            </div>

            <div class="note-box">
                <p><strong>What to do next:</strong> Please check your payment details and try again. If the problem persists, contact your bank or try a different payment method.</p>
            </div>

            <p class="message">If you need assistance, please don't hesitate to contact our support team.</p>
            """.formatted(
                payment.getUser().getName(),
                payment.getTotalAmount()
            );

        return getEmailWrapper("danger", "Payment Failed", "Your payment could not be processed", content);
    }

    private String buildInvoiceEmail(Invoice invoice) {
        String content = """
            <p class="greeting">Dear %s,</p>
            <p class="message">Please find your invoice details below for your recent stay with us.</p>

            <div class="details-card">
                <h3>Invoice #%s</h3>
                <table style="width: 100%%; border-collapse: collapse;">
                    <tr class="detail-row">
                        <td class="detail-label">Room Charges</td>
                        <td class="detail-value" style="text-align: right;">₹%,d</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Service Charges</td>
                        <td class="detail-value" style="text-align: right;">₹%,d</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">CGST (9%%)</td>
                        <td class="detail-value" style="text-align: right;">₹%,d</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">SGST (9%%)</td>
                        <td class="detail-value" style="text-align: right;">₹%,d</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Discount</td>
                        <td class="detail-value" style="text-align: right; color: #2e7d32;">-₹%,d</td>
                    </tr>
                </table>
                <div class="total-row">
                    <table style="width: 100%%;">
                        <tr>
                            <td class="detail-label"><strong>Grand Total</strong></td>
                            <td class="detail-value" style="text-align: right; color: #6a1b9a; font-size: 20px;">₹%,d</td>
                        </tr>
                    </table>
                </div>
            </div>

            <p class="message">Thank you for choosing %s. We hope to see you again!</p>
            """.formatted(
                invoice.getUser().getName(),
                invoice.getInvoiceNumber(),
                invoice.getRoomCharges(),
                invoice.getServiceCharges(),
                invoice.getCgst(),
                invoice.getSgst(),
                invoice.getDiscount(),
                invoice.getGrandTotal(),
                hotelName
            );

        return getEmailWrapper("", "Your Invoice", "Invoice #" + invoice.getInvoiceNumber(), content);
    }

    private String buildWelcomeEmail(User user) {
        String content = """
            <p class="greeting">Dear %s,</p>
            <p class="message">Welcome to %s! We're thrilled to have you join our family.</p>

            <div class="details-card">
                <h3>What You Can Do</h3>
                <table style="width: 100%%; border-collapse: collapse;">
                    <tr class="detail-row">
                        <td class="detail-label">Browse Rooms</td>
                        <td class="detail-value" style="text-align: right;">Explore our luxurious accommodations</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Book Instantly</td>
                        <td class="detail-value" style="text-align: right;">Reserve your perfect room with ease</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Manage Bookings</td>
                        <td class="detail-value" style="text-align: right;">View and manage all your reservations</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Exclusive Offers</td>
                        <td class="detail-value" style="text-align: right;">Access member-only promotions</td>
                    </tr>
                </table>
            </div>

            <p class="message">Start exploring and book your first stay today. We can't wait to host you!</p>
            """.formatted(
                user.getName(),
                hotelName
            );

        return getEmailWrapper("", "Welcome!", "Your account is ready", content);
    }

    private String buildPasswordResetEmail(User user, String resetToken) {
        String content = """
            <p class="greeting">Dear %s,</p>
            <p class="message">We received a request to reset your password. Use the code below to proceed:</p>

            <div class="details-card" style="text-align: center;">
                <h3>Your Reset Code</h3>
                <p style="font-size: 32px; font-weight: 700; color: #6a1b9a; letter-spacing: 4px; margin: 16px 0;">%s</p>
                <p style="font-size: 13px; color: #888;">This code expires in 15 minutes</p>
            </div>

            <div class="note-box">
                <p><strong>Didn't request this?</strong> If you didn't request a password reset, please ignore this email. Your password will remain unchanged.</p>
            </div>

            <p class="message">For security, never share this code with anyone.</p>
            """.formatted(
                user.getName(),
                resetToken
            );

        return getEmailWrapper("warning", "Password Reset", "Reset your password", content);
    }

    private String buildReviewRequestEmail(Reservation reservation) {
        String content = """
            <p class="greeting">Dear %s,</p>
            <p class="message">Thank you for staying with us! We hope you had a wonderful experience at %s.</p>

            <div class="details-card">
                <h3>Your Recent Stay</h3>
                <table style="width: 100%%; border-collapse: collapse;">
                    <tr class="detail-row">
                        <td class="detail-label">Room</td>
                        <td class="detail-value" style="text-align: right;">%s</td>
                    </tr>
                    <tr class="detail-row">
                        <td class="detail-label">Check-out Date</td>
                        <td class="detail-value" style="text-align: right;">%s</td>
                    </tr>
                </table>
            </div>

            <p class="message">We'd love to hear about your experience! Your feedback helps us improve and helps other travelers make informed decisions.</p>

            <div style="text-align: center; margin: 24px 0;">
                <p style="font-size: 32px; margin-bottom: 8px;">⭐⭐⭐⭐⭐</p>
                <p style="font-size: 14px; color: #888;">Rate your stay and share your thoughts</p>
            </div>

            <p class="message">Log in to your account to leave a review. It only takes a minute!</p>
            """.formatted(
                reservation.getUser().getName(),
                hotelName,
                reservation.getRoom().getName(),
                reservation.getCheckOutDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            );

        return getEmailWrapper("", "How Was Your Stay?", "We'd love your feedback", content);
    }

    private String buildPromotionalEmail(User user, String content) {
        String emailContent = """
            <p class="greeting">Dear %s,</p>
            %s

            <div style="text-align: center; margin: 24px 0;">
                <p style="font-size: 14px; color: #888;">Don't miss out on this exclusive offer!</p>
            </div>
            """.formatted(user.getName(), content);

        return getEmailWrapper("", "Special Offer!", "Exclusive deal just for you", emailContent);
    }
}
