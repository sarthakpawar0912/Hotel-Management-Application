package com.sarthakpawar.CONTROLLER.Customer;

import com.sarthakpawar.DTO.ReviewDto;
import com.sarthakpawar.ENTITY.User;
import com.sarthakpawar.SERVICES.ADMIN.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/reviews")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CustomerReviewController {

    private final ReviewService reviewService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewDto) {
        reviewDto.setUserId(getCurrentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(reviewDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long id, @RequestBody ReviewDto reviewDto) {
        return ResponseEntity.ok(reviewService.updateReview(id, reviewDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ReviewDto>> getApprovedReviewsByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(reviewService.getApprovedReviewsByRoom(roomId));
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<List<ReviewDto>> getMyReviews() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<ReviewDto> getReviewByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reviewService.getReviewByReservation(reservationId));
    }

    @GetMapping("/can-review/{roomId}")
    public ResponseEntity<Map<String, Boolean>> canReviewRoom(@PathVariable Long roomId) {
        Long userId = getCurrentUserId();
        boolean canReview = reviewService.canUserReviewRoom(userId, roomId);
        return ResponseEntity.ok(Map.of("canReview", canReview));
    }

    @GetMapping("/room/{roomId}/rating")
    public ResponseEntity<Map<String, Object>> getRoomRating(@PathVariable Long roomId) {
        Double avgRating = reviewService.getAverageRatingByRoom(roomId);
        Long reviewCount = reviewService.getReviewCountByRoom(roomId);
        Map<String, Double> breakdown = reviewService.getRatingBreakdownByRoom(roomId);

        return ResponseEntity.ok(Map.of(
            "averageRating", avgRating,
            "reviewCount", reviewCount,
            "breakdown", breakdown
        ));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ReviewDto>> getRecentReviews() {
        return ResponseEntity.ok(reviewService.getRecentApprovedReviews());
    }
}
