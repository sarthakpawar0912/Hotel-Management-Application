package com.sarthakpawar.CONTROLLER.Customer;

import com.sarthakpawar.DTO.PromotionDto;
import com.sarthakpawar.ENTITY.User;
import com.sarthakpawar.SERVICES.ADMIN.promotion.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/promotions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CustomerPromotionController {

    private final PromotionService promotionService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }

    @GetMapping("/active")
    public ResponseEntity<List<PromotionDto>> getActivePromotions() {
        return ResponseEntity.ok(promotionService.getValidPromotions());
    }

    @GetMapping("/valid")
    public ResponseEntity<List<PromotionDto>> getValidPromotions() {
        return ResponseEntity.ok(promotionService.getValidPromotions());
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<PromotionDto> validatePromoCode(@PathVariable String code) {
        PromotionDto promotion = promotionService.getPromotionByPromoCode(code);
        if (promotion != null && promotion.getIsActive() &&
            promotion.getEndDate().isAfter(LocalDate.now())) {
            return ResponseEntity.ok(promotion);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePromoCodeFull(
            @RequestParam String promoCode,
            @RequestParam Long bookingAmount,
            @RequestParam Integer nights,
            @RequestParam(required = false) String roomType,
            @RequestParam String checkInDate) {
        Long userId = getCurrentUserId();
        Map<String, Object> result = promotionService.validatePromoCode(
            promoCode, userId, bookingAmount, nights, roomType, LocalDate.parse(checkInDate)
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> applyPromoCode(
            @RequestParam Long reservationId,
            @RequestParam String promoCode) {
        // This would be implemented to apply promo code to a reservation
        Map<String, Object> result = Map.of(
            "success", true,
            "message", "Promo code applied successfully"
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/discount")
    public ResponseEntity<Map<String, Long>> calculateDiscount(
            @PathVariable Long id,
            @RequestParam Long bookingAmount) {
        Long discount = promotionService.calculateDiscount(id, bookingAmount);
        return ResponseEntity.ok(Map.of("discount", discount));
    }
}
