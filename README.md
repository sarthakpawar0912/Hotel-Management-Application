# Hotel Management System - Backend API

A comprehensive **Hotel Management System** built with **Spring Boot** featuring Razorpay payment integration, JWT authentication, and complete booking management.

---

## Table of Contents

1. [Overview](#1-overview)
2. [Features](#2-features)
3. [Tech Stack](#3-tech-stack)
4. [System Architecture](#4-system-architecture)
5. [Database Schema](#5-database-schema)
6. [API Documentation](#6-api-documentation)
7. [Razorpay Payment Flow](#7-razorpay-payment-flow)
8. [Promo Code System](#8-promo-code-system)
9. [Email Notifications](#9-email-notifications)
10. [How to Run Locally](#10-how-to-run-locally)
11. [Security & Roles](#11-security--roles)
12. [Future Enhancements](#12-future-enhancements)
13. [Conclusion](#13-conclusion)

---

## 1. Overview

This is a full-featured hotel management backend API that handles:
- Room inventory and availability management
- Customer bookings with date validation
- Online payments via Razorpay
- Promotional discounts with promo codes
- Guest check-in/check-out tracking
- Invoice generation with GST
- Review and rating system
- Email notifications for all booking events

**Frontend Repository:** [Hotel-Management-Web](https://github.com/sarthakpawar0912/Hotel-Management-Web.git)

---

## 2. Features

### For Customers
- Browse available rooms with filters (type, price, capacity)
- Book rooms with date selection
- Apply promo codes for discounts
- Pay online via Razorpay
- View booking history and invoices
- Submit reviews after checkout
- Receive email notifications

### For Administrators
- Manage room inventory (add, update, delete)
- View and manage all reservations
- Approve/reject booking requests
- Guest check-in and check-out
- Create and manage promotions
- View payment reports
- Respond to customer reviews
- Generate invoices

---

## 3. Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend Framework** | Spring Boot 3.x |
| **Language** | Java 17+ |
| **Database** | MySQL 8.0 |
| **Security** | Spring Security + JWT |
| **Payment Gateway** | Razorpay (Test Mode) |
| **Email Service** | JavaMailSender (Gmail SMTP) |
| **ORM** | Spring Data JPA / Hibernate |
| **Build Tool** | Maven |
| **API Documentation** | RESTful APIs |

---

## 4. System Architecture

```
+-------------------+         +--------------------+         +-------------------+
|   Angular UI      |<------->|  Spring Boot       |<------->|     MySQL         |
|   (Frontend)      |   REST  |  Backend APIs      |   JPA   |    Database       |
+-------------------+         +---------+----------+         +-------------------+
                                        |
                    +-------------------+-------------------+
                    |                   |                   |
                    v                   v                   v
            +--------------+   +--------------+   +--------------+
            |   Razorpay   |   | Gmail SMTP   |   | JWT Token    |
            |   Gateway    |   | Email Server |   | Validation   |
            +--------------+   +--------------+   +--------------+
```

### Request Flow
1. Customer/Admin sends request to Angular Frontend
2. Frontend makes REST API calls with JWT Token
3. Backend validates JWT, processes request
4. Database operations via JPA
5. External integrations (Razorpay, Email)
6. Response returned to Frontend

---

## 5. Database Schema

### Core Entities

| Entity | Description |
|--------|-------------|
| `User` | Stores customer and admin accounts |
| `Room` | Hotel room details (type, price, capacity) |
| `Reservation` | Booking information with dates and status |
| `Payment` | Payment records with Razorpay integration |
| `Invoice` | Generated invoices with GST breakdown |
| `Guest` | Guest details for check-in |
| `Review` | Customer reviews and ratings |
| `Promotion` | Promo codes and discount rules |
| `Notification` | User notifications |

### Key Relationships
- User has many Reservations
- Room has many Reservations
- Reservation has one Payment
- Reservation has one Invoice
- Reservation has many Guests
- User has many Reviews
- Promotion has many PromotionUsages

---

## 6. API Documentation

### Authentication APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Customer APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/customer/rooms/{page}` | Get paginated rooms |
| GET | `/api/customer/rooms/search` | Search rooms with filters |
| POST | `/api/customer/book` | Create new booking |
| GET | `/api/customer/bookings/{userId}/{page}` | Get user's bookings |
| PUT | `/api/customer/bookings/{id}/cancel` | Cancel booking |
| POST | `/api/customer/payments/create-order/{reservationId}` | Create Razorpay order |
| POST | `/api/customer/payments/verify` | Verify payment |
| GET | `/api/customer/invoices/reservation/{id}` | Get invoice |
| GET | `/api/customer/promotions/active` | Get active promotions |
| GET | `/api/customer/promotions/validate/{code}` | Validate promo code |
| POST | `/api/customer/reviews` | Submit review |
| GET | `/api/customer/notifications` | Get notifications |

### Admin APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/room` | Add new room |
| PUT | `/api/admin/room/{id}` | Update room |
| DELETE | `/api/admin/room/{id}` | Delete room |
| GET | `/api/admin/reservations/{page}` | Get all reservations |
| PUT | `/api/admin/reservation/{id}/{status}` | Update booking status |
| POST | `/api/admin/check-in/{reservationId}` | Check-in guest |
| POST | `/api/admin/check-out/{reservationId}` | Check-out guest |
| GET | `/api/admin/guests` | Get all guests |
| POST | `/api/admin/promotions` | Create promotion |
| GET | `/api/admin/payments` | Get all payments |
| GET | `/api/admin/invoices` | Get all invoices |
| GET | `/api/admin/reviews` | Get all reviews |
| GET | `/api/admin/reports/revenue` | Get revenue reports |

---

## 7. Razorpay Payment Flow

### Why Razorpay?
- Secure payment processing
- Multiple payment methods (Cards, UPI, NetBanking, Wallets)
- Test mode for development
- Easy integration with webhooks

### Payment Flow

```
+----------+     +----------+     +----------+     +----------+
| Customer |     | Frontend |     | Backend  |     | Razorpay |
+----+-----+     +----+-----+     +----+-----+     +----+-----+
     |                |                |                |
     | 1. Click Pay   |                |                |
     |--------------->|                |                |
     |                | 2. Create Order|                |
     |                |--------------->|                |
     |                |                | 3. Create Order|
     |                |                |--------------->|
     |                |                | 4. Order ID    |
     |                |                |<---------------|
     |                | 5. Order Details               |
     |                |<---------------|                |
     | 6. Open Razorpay Checkout       |                |
     |<---------------|                |                |
     | 7. Enter Payment Details        |                |
     |-------------------------------------------->|
     | 8. Payment Success + Signature  |                |
     |<--------------------------------------------|
     |                | 9. Verify Signature             |
     |                |--------------->|                |
     |                |                | 10. HMAC Check |
     |                |                |--------------->|
     |                |                | 11. Verified   |
     |                |                |<---------------|
     |                | 12. Payment Confirmed           |
     |                |<---------------|                |
     | 13. Success    |                |                |
     |<---------------|                |                |
```

### Step-by-Step Process

1. **Customer Initiates Payment**
   - Customer clicks "Pay Now" on their booking

2. **Create Razorpay Order (Backend)**
   ```java
   // PaymentServiceImpl.java
   JSONObject orderRequest = new JSONObject();
   orderRequest.put("amount", totalAmount * 100); // Amount in paise
   orderRequest.put("currency", "INR");
   orderRequest.put("receipt", "rcpt_" + reservationId);
   Order razorpayOrder = razorpayClient.orders.create(orderRequest);
   ```

3. **Frontend Opens Razorpay Checkout**
   ```javascript
   const options = {
     key: razorpayKeyId,
     amount: order.amount,
     currency: 'INR',
     order_id: order.orderId,
     handler: function(response) {
       // Verify payment
     }
   };
   const rzp = new Razorpay(options);
   rzp.open();
   ```

4. **Payment Verification (Backend)**
   ```java
   // Verify signature using HMAC SHA256
   String generatedSignature = generateSignature(orderId, paymentId);
   if (generatedSignature.equals(razorpaySignature)) {
     // Payment verified - update database
     payment.setPaymentStatus(PaymentStatus.COMPLETED);
   }
   ```

### Razorpay Test Credentials
Get your test keys from: https://dashboard.razorpay.com/app/keys

```properties
razorpay.key_id=rzp_test_xxxxxxxxxxxxx
razorpay.key_secret=your_secret_key
```

### Test Card Details
| Card Number | Expiry | CVV |
|-------------|--------|-----|
| 4111 1111 1111 1111 | Any future date | Any 3 digits |

---

## 8. Promo Code System

### How Promo Codes Work

```
+-------------------------------------------------------------+
|                    PROMO CODE VALIDATION                     |
+-------------------------------------------------------------+
|                                                             |
|  1. Admin Creates Promo Code                                |
|     - Code: SUMMER20                                        |
|     - Discount: 20% (or Fixed Rs.500)                       |
|     - Valid: 01-Jan to 31-Mar                               |
|     - Min Booking: Rs.2000                                  |
|     - Max Discount: Rs.1000                                 |
|     - Usage Limit: 100 times                                |
|                                                             |
|  2. Customer Applies Code                                   |
|     - Frontend sends: promoCode + bookingAmount             |
|                                                             |
|  3. Backend Validates (PromotionServiceImpl.java)           |
|     - Code exists? YES                                      |
|     - Code is active? YES                                   |
|     - Not expired? YES                                      |
|     - Meets min amount? YES                                 |
|     - User hasn't exceeded limit? YES                       |
|     - Room type applicable? YES                             |
|     - All checks passed? --> Calculate discount             |
|                                                             |
|  4. Discount Calculation                                    |
|     - PERCENTAGE: amount x discountValue / 100              |
|     - FIXED: discountValue directly                         |
|     - Cap at maxDiscountAmount                              |
|                                                             |
|  5. Apply to Payment                                        |
|     - finalAmount = bookingAmount - discount + GST          |
|                                                             |
+-------------------------------------------------------------+
```

### Validation Rules

```java
// PromotionServiceImpl.java - validatePromoCode()

// 1. Check if code exists and is valid
Promotion promotion = promotionRepository.findValidPromoCode(promoCode, LocalDate.now());

// 2. Check per-user usage limit
if (!canUserUsePromotion(promotion.getId(), userId)) {
    return "You have already used this promo code maximum times";
}

// 3. Check minimum booking amount
if (bookingAmount < promotion.getMinBookingAmount()) {
    return "Minimum booking amount is Rs." + promotion.getMinBookingAmount();
}

// 4. Check minimum nights
if (nights < promotion.getMinNights()) {
    return "Minimum " + promotion.getMinNights() + " nights required";
}

// 5. Check applicable room types
if (!isRoomTypeApplicable(promotion, roomType)) {
    return "This promo code is not applicable for this room type";
}

// 6. Calculate discount
Long discount = calculateDiscount(promotion.getId(), bookingAmount);
```

### Why Backend Validation?

**Frontend CANNOT be trusted because:**
- Users can modify JavaScript
- Network requests can be intercepted
- Discount amounts could be manipulated

**Backend ensures:**
- All validation happens server-side
- Database records promo usage
- Final amount calculated securely
- Prevents abuse and fraud

---

## 9. Email Notifications

### Email Types

| Event | Recipient | Content |
|-------|-----------|---------|
| Registration | Customer | Welcome message |
| Booking Created | Customer | Booking confirmation |
| Booking Approved | Customer | Approval + payment link |
| Booking Rejected | Customer | Rejection reason |
| Payment Success | Customer | Receipt + invoice |
| Check-in | Customer | Room details |
| Check-out | Customer | Thank you + review request |

### Email Configuration

```properties
# application.properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Gmail App Password Setup
1. Enable 2-Step Verification in Google Account
2. Go to Security -> App Passwords
3. Generate password for "Mail"
4. Use this password in `EMAIL_PASSWORD`

---

## 10. How to Run Locally

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0
- Node.js 18+ (for frontend)

### Step 1: Clone Repositories

```bash
# Backend
git clone https://github.com/sarthakpawar0912/Hotel-Management-Application.git
cd Hotel-Management-Application

# Frontend (separate terminal)
git clone https://github.com/sarthakpawar0912/Hotel-Management-Web.git
cd Hotel-Management-Web
```

### Step 2: Setup MySQL Database

```sql
CREATE DATABASE hotel_management_system;
```

### Step 3: Configure Backend

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/hotel_management_system
spring.datasource.username=your_username
spring.datasource.password=your_password

# Email (Gmail)
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password

# Razorpay (Get from dashboard.razorpay.com)
razorpay.key_id=rzp_test_xxxxxxxxxxxxx
razorpay.key_secret=your_razorpay_secret
```

### Step 4: Run Backend

```bash
cd Hotel-Management-Application
mvn spring-boot:run
```
Backend runs at: `http://localhost:8080`

### Step 5: Run Frontend

```bash
cd Hotel-Management-Web
npm install
ng serve
```
Frontend runs at: `http://localhost:4200`

### Step 6: Access Application

- **Customer Portal:** http://localhost:4200
- **Admin Panel:** http://localhost:4200 (login as admin)
- **API Base URL:** http://localhost:8080/api

### Default Credentials

```
Admin:
  Email: admin@hotel.com
  Password: admin123

Customer:
  Register at: http://localhost:4200/register
```

---

## 11. Security & Roles

### JWT Authentication

```
Every request is intercepted and validated:

1. Extract JWT from Authorization header
2. Validate token signature and expiry
3. Extract user details from token
4. Set authentication in SecurityContext
5. Allow/Deny request based on role
```

### Role-Based Access

| Role | Access |
|------|--------|
| **CUSTOMER** | Browse rooms, book, pay, review |
| **ADMIN** | Full access to all features |

### Endpoint Security

```java
// WebSecurityConfiguration.java
.requestMatchers("/api/auth/**").permitAll()        // Public
.requestMatchers("/api/customer/**").hasRole("CUSTOMER")
.requestMatchers("/api/admin/**").hasRole("ADMIN")
.anyRequest().authenticated()
```

### Password Security
- BCrypt hashing
- Salt automatically generated
- No plain text storage

---

## 12. Future Enhancements

- **Production Razorpay:** Switch to live API keys
- **Wallet System:** Pre-paid balance for quick payments
- **Loyalty Points:** Earn points on bookings
- **Mobile App:** React Native / Flutter app
- **Multi-Property:** Support multiple hotels
- **Dynamic Pricing:** Seasonal rate adjustments
- **Chat Support:** Real-time customer support
- **Analytics Dashboard:** Advanced reporting

---

## 13. Conclusion

This Hotel Management System demonstrates:

### Technical Skills
- Full-stack development (Spring Boot + Angular)
- Payment gateway integration (Razorpay)
- Secure authentication (JWT)
- Database design (MySQL + JPA)
- RESTful API development
- Email service integration

### Business Logic
- Complete booking lifecycle
- GST tax calculation (CGST + SGST)
- Promotional discount system
- Invoice generation
- Review and rating system

### Best Practices
- Clean code architecture
- Service layer pattern
- DTO pattern for data transfer
- Environment-based configuration
- Proper error handling

---

## Author

**Sarthak Pawar**
- GitHub: [@sarthakpawar0912](https://github.com/sarthakpawar0912)

---

## License

This project is for educational and portfolio purposes.
