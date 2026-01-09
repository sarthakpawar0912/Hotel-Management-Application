package com.sarthakpawar.SERVICES.notification;

import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.ENTITY.Payment;
import com.sarthakpawar.ENTITY.Invoice;
import com.sarthakpawar.ENTITY.User;

public interface EmailService {

    void sendBookingConfirmation(Reservation reservation);

    void sendBookingCancellation(Reservation reservation);

    void sendCheckInReminder(Reservation reservation);

    void sendCheckOutReminder(Reservation reservation);

    void sendPaymentConfirmation(Payment payment);

    void sendPaymentFailure(Payment payment);

    void sendInvoice(Invoice invoice);

    void sendWelcomeEmail(User user);

    void sendPasswordReset(User user, String resetToken);

    void sendReviewRequest(Reservation reservation);

    void sendPromotionalEmail(User user, String subject, String content);

    void sendGenericEmail(String to, String subject, String body);
}
