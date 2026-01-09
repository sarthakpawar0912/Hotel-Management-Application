package com.sarthakpawar.SERVICES.ADMIN.promotion;

import com.sarthakpawar.DTO.PromotionDto;
import com.sarthakpawar.ENTITY.Promotion;
import com.sarthakpawar.ENTITY.PromotionUsage;
import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.ENTITY.User;
import com.sarthakpawar.ENUMS.DiscountType;
import com.sarthakpawar.REPOSITORY.PromotionRepository;
import com.sarthakpawar.REPOSITORY.PromotionUsageRepository;
import com.sarthakpawar.REPOSITORY.ReservationRepository;
import com.sarthakpawar.REPOSITORY.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionUsageRepository promotionUsageRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public PromotionDto createPromotion(PromotionDto promotionDto) {
        // Check if promo code already exists
        if (promotionDto.getPromoCode() != null &&
            promotionRepository.findByPromoCode(promotionDto.getPromoCode()).isPresent()) {
            throw new RuntimeException("Promo code already exists");
        }

        Promotion promotion = new Promotion();
        mapDtoToEntity(promotionDto, promotion);
        promotion.setUsageCount(0);

        Promotion savedPromotion = promotionRepository.save(promotion);
        return savedPromotion.getPromotionDto();
    }

    @Override
    @Transactional
    public PromotionDto updatePromotion(Long id, PromotionDto promotionDto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));

        mapDtoToEntity(promotionDto, promotion);

        Promotion updatedPromotion = promotionRepository.save(promotion);
        return updatedPromotion.getPromotionDto();
    }

    @Override
    public PromotionDto getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));
        return promotion.getPromotionDto();
    }

    @Override
    public PromotionDto getPromotionByPromoCode(String promoCode) {
        Promotion promotion = promotionRepository.findByPromoCode(promoCode)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));
        return promotion.getPromotionDto();
    }

    @Override
    public List<PromotionDto> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(Promotion::getPromotionDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionDto> getActivePromotions() {
        return promotionRepository.findByIsActiveTrue().stream()
                .map(Promotion::getPromotionDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionDto> getValidPromotions() {
        return promotionRepository.findValidPromotions(LocalDate.now()).stream()
                .map(Promotion::getPromotionDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionDto> getExpiredPromotions() {
        return promotionRepository.findExpiredPromotions(LocalDate.now()).stream()
                .map(Promotion::getPromotionDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionDto> getUpcomingPromotions() {
        return promotionRepository.findUpcomingPromotions(LocalDate.now()).stream()
                .map(Promotion::getPromotionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PromotionDto activatePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));

        promotion.setIsActive(true);
        Promotion updatedPromotion = promotionRepository.save(promotion);
        return updatedPromotion.getPromotionDto();
    }

    @Override
    @Transactional
    public PromotionDto deactivatePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));

        promotion.setIsActive(false);
        Promotion updatedPromotion = promotionRepository.save(promotion);
        return updatedPromotion.getPromotionDto();
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new EntityNotFoundException("Promotion not found");
        }
        promotionRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> validatePromoCode(String promoCode, Long userId, Long bookingAmount, Integer nights, String roomType, LocalDate checkInDate) {
        Map<String, Object> result = new HashMap<>();

        Promotion promotion = promotionRepository.findValidPromoCode(promoCode, LocalDate.now())
                .orElse(null);

        if (promotion == null) {
            result.put("valid", false);
            result.put("message", "Invalid or expired promo code");
            return result;
        }

        // Check usage limit per user
        if (!canUserUsePromotion(promotion.getId(), userId)) {
            result.put("valid", false);
            result.put("message", "You have already used this promo code maximum times");
            return result;
        }

        // Check minimum booking amount
        if (promotion.getMinBookingAmount() != null && bookingAmount < promotion.getMinBookingAmount()) {
            result.put("valid", false);
            result.put("message", "Minimum booking amount is ₹" + promotion.getMinBookingAmount());
            return result;
        }

        // Check minimum nights
        if (promotion.getMinNights() != null && nights < promotion.getMinNights()) {
            result.put("valid", false);
            result.put("message", "Minimum " + promotion.getMinNights() + " nights required");
            return result;
        }

        // Check maximum nights
        if (promotion.getMaxNights() != null && nights > promotion.getMaxNights()) {
            result.put("valid", false);
            result.put("message", "Maximum " + promotion.getMaxNights() + " nights allowed");
            return result;
        }

        // Check applicable room types
        if (promotion.getApplicableRoomTypes() != null && !promotion.getApplicableRoomTypes().isEmpty()) {
            String[] applicableTypes = promotion.getApplicableRoomTypes().split(",");
            boolean roomTypeValid = false;
            for (String type : applicableTypes) {
                if (type.trim().equalsIgnoreCase(roomType)) {
                    roomTypeValid = true;
                    break;
                }
            }
            if (!roomTypeValid) {
                result.put("valid", false);
                result.put("message", "This promo code is not applicable for this room type");
                return result;
            }
        }

        // Check advance booking days
        if (promotion.getAdvanceBookingDays() != null) {
            long daysUntilCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), checkInDate);
            if (daysUntilCheckIn < promotion.getAdvanceBookingDays()) {
                result.put("valid", false);
                result.put("message", "Book at least " + promotion.getAdvanceBookingDays() + " days in advance");
                return result;
            }
        }

        // Check last minute booking
        if (promotion.getLastMinuteDays() != null) {
            long daysUntilCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), checkInDate);
            if (daysUntilCheckIn > promotion.getLastMinuteDays()) {
                result.put("valid", false);
                result.put("message", "This is a last-minute deal for bookings within " + promotion.getLastMinuteDays() + " days");
                return result;
            }
        }

        // Calculate discount
        Long discount = calculateDiscount(promotion.getId(), bookingAmount);

        result.put("valid", true);
        result.put("promotionId", promotion.getId());
        result.put("promotionName", promotion.getName());
        result.put("discountType", promotion.getDiscountType());
        result.put("discountValue", promotion.getDiscountValue());
        result.put("discountAmount", discount);
        result.put("message", "Promo code applied successfully!");

        return result;
    }

    @Override
    public Long calculateDiscount(Long promotionId, Long bookingAmount) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));

        Long discount = 0L;

        if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = Math.round(bookingAmount * promotion.getDiscountValue() / 100);
        } else {
            discount = promotion.getDiscountValue().longValue();
        }

        // Apply max discount cap
        if (promotion.getMaxDiscountAmount() != null && discount > promotion.getMaxDiscountAmount()) {
            discount = promotion.getMaxDiscountAmount();
        }

        return discount;
    }

    @Override
    @Transactional
    public void recordPromoUsage(Long promotionId, Long userId, Long reservationId, Long discountApplied) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        PromotionUsage usage = new PromotionUsage();
        usage.setPromotion(promotion);
        usage.setUser(user);
        usage.setDiscountApplied(discountApplied);

        if (reservationId != null) {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
            usage.setReservation(reservation);
        }

        promotionUsageRepository.save(usage);

        // Increment usage count
        promotion.setUsageCount(promotion.getUsageCount() + 1);
        promotionRepository.save(promotion);
    }

    @Override
    public boolean canUserUsePromotion(Long promotionId, Long userId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));

        Long userUsageCount = promotionUsageRepository.countUserPromotionUsage(promotionId, userId);

        return promotion.getPerUserLimit() == null || userUsageCount < promotion.getPerUserLimit();
    }

    private void mapDtoToEntity(PromotionDto dto, Promotion entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPromoCode(dto.getPromoCode());
        entity.setDiscountType(dto.getDiscountType());
        entity.setDiscountValue(dto.getDiscountValue());
        entity.setMinBookingAmount(dto.getMinBookingAmount());
        entity.setMaxDiscountAmount(dto.getMaxDiscountAmount());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setUsageLimit(dto.getUsageLimit());
        entity.setPerUserLimit(dto.getPerUserLimit());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        entity.setMinNights(dto.getMinNights());
        entity.setMaxNights(dto.getMaxNights());
        entity.setApplicableRoomTypes(dto.getApplicableRoomTypes());
        entity.setApplicableDays(dto.getApplicableDays());
        entity.setAdvanceBookingDays(dto.getAdvanceBookingDays());
        entity.setLastMinuteDays(dto.getLastMinuteDays());
    }
}
