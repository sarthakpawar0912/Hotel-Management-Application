package com.sarthakpawar.ENTITY;

import com.sarthakpawar.DTO.PromotionDto;
import com.sarthakpawar.ENUMS.DiscountType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(unique = true)
    private String promoCode;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Double discountValue; // Percentage or flat amount

    private Long minBookingAmount;
    private Long maxDiscountAmount;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer usageLimit; // Total usage limit
    private Integer usageCount = 0;

    private Integer perUserLimit = 1; // Usage limit per user

    private Boolean isActive = true;

    // Applicable conditions
    private Integer minNights;
    private Integer maxNights;
    private String applicableRoomTypes; // Comma-separated room types
    private String applicableDays; // Comma-separated days (MON,TUE,etc.)

    // Early bird / Last minute
    private Integer advanceBookingDays; // Book X days in advance
    private Integer lastMinuteDays; // Book within X days

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public PromotionDto getPromotionDto() {
        PromotionDto dto = new PromotionDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setPromoCode(promoCode);
        dto.setDiscountType(discountType);
        dto.setDiscountValue(discountValue);
        dto.setMinBookingAmount(minBookingAmount);
        dto.setMaxDiscountAmount(maxDiscountAmount);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setUsageLimit(usageLimit);
        dto.setUsageCount(usageCount);
        dto.setPerUserLimit(perUserLimit);
        dto.setIsActive(isActive);
        dto.setMinNights(minNights);
        dto.setMaxNights(maxNights);
        dto.setApplicableRoomTypes(applicableRoomTypes);
        dto.setApplicableDays(applicableDays);
        dto.setAdvanceBookingDays(advanceBookingDays);
        dto.setLastMinuteDays(lastMinuteDays);
        return dto;
    }

    public boolean isValidNow() {
        LocalDate today = LocalDate.now();
        return isActive &&
               (startDate == null || !today.isBefore(startDate)) &&
               (endDate == null || !today.isAfter(endDate)) &&
               (usageLimit == null || usageCount < usageLimit);
    }
}
