package com.sarthakpawar.DTO;

import com.sarthakpawar.ENUMS.DiscountType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PromotionDto {

    private Long id;
    private String name;
    private String description;
    private String promoCode;
    private DiscountType discountType;
    private Double discountValue;
    private Long minBookingAmount;
    private Long maxDiscountAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer usageLimit;
    private Integer usageCount;
    private Integer perUserLimit;
    private Boolean isActive;
    private Integer minNights;
    private Integer maxNights;
    private String applicableRoomTypes;
    private String applicableDays;
    private Integer advanceBookingDays;
    private Integer lastMinuteDays;
}
