package com.sarthakpawar.CONTROLLER.ADMIN;

import com.sarthakpawar.DTO.PromotionDto;
import com.sarthakpawar.SERVICES.ADMIN.promotion.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/promotions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<PromotionDto> createPromotion(@RequestBody PromotionDto promotionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promotionService.createPromotion(promotionDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionDto> updatePromotion(@PathVariable Long id, @RequestBody PromotionDto promotionDto) {
        return ResponseEntity.ok(promotionService.updatePromotion(id, promotionDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionDto> getPromotionById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotionById(id));
    }

    @GetMapping("/code/{promoCode}")
    public ResponseEntity<PromotionDto> getPromotionByCode(@PathVariable String promoCode) {
        return ResponseEntity.ok(promotionService.getPromotionByPromoCode(promoCode));
    }

    @GetMapping
    public ResponseEntity<List<PromotionDto>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @GetMapping("/active")
    public ResponseEntity<List<PromotionDto>> getActivePromotions() {
        return ResponseEntity.ok(promotionService.getActivePromotions());
    }

    @GetMapping("/valid")
    public ResponseEntity<List<PromotionDto>> getValidPromotions() {
        return ResponseEntity.ok(promotionService.getValidPromotions());
    }

    @GetMapping("/expired")
    public ResponseEntity<List<PromotionDto>> getExpiredPromotions() {
        return ResponseEntity.ok(promotionService.getExpiredPromotions());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<PromotionDto>> getUpcomingPromotions() {
        return ResponseEntity.ok(promotionService.getUpcomingPromotions());
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<PromotionDto> activatePromotion(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.activatePromotion(id));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<PromotionDto> deactivatePromotion(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.deactivatePromotion(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePromoCode(
            @RequestParam String promoCode,
            @RequestParam Long userId,
            @RequestParam Long bookingAmount,
            @RequestParam Integer nights,
            @RequestParam(required = false) String roomType,
            @RequestParam String checkInDate) {
        Map<String, Object> result = promotionService.validatePromoCode(
            promoCode, userId, bookingAmount, nights, roomType, LocalDate.parse(checkInDate)
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
