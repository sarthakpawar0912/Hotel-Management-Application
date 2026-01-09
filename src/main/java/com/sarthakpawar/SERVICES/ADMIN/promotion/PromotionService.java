package com.sarthakpawar.SERVICES.ADMIN.promotion;

import com.sarthakpawar.DTO.PromotionDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PromotionService {

    PromotionDto createPromotion(PromotionDto promotionDto);

    PromotionDto updatePromotion(Long id, PromotionDto promotionDto);

    PromotionDto getPromotionById(Long id);

    PromotionDto getPromotionByPromoCode(String promoCode);

    List<PromotionDto> getAllPromotions();

    List<PromotionDto> getActivePromotions();

    List<PromotionDto> getValidPromotions();

    List<PromotionDto> getExpiredPromotions();

    List<PromotionDto> getUpcomingPromotions();

    PromotionDto activatePromotion(Long id);

    PromotionDto deactivatePromotion(Long id);

    void deletePromotion(Long id);

    // Promo code validation and application
    Map<String, Object> validatePromoCode(String promoCode, Long userId, Long bookingAmount, Integer nights, String roomType, LocalDate checkInDate);

    Long calculateDiscount(Long promotionId, Long bookingAmount);

    void recordPromoUsage(Long promotionId, Long userId, Long reservationId, Long discountApplied);

    boolean canUserUsePromotion(Long promotionId, Long userId);
}
