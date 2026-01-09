package com.sarthakpawar.CONTROLLER.ADMIN;

import com.sarthakpawar.DTO.ReviewDto;
import com.sarthakpawar.SERVICES.ADMIN.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(reviewService.getReviewsByRoom(roomId));
    }

    @GetMapping("/room/{roomId}/approved")
    public ResponseEntity<List<ReviewDto>> getApprovedReviewsByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(reviewService.getApprovedReviewsByRoom(roomId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ReviewDto>> getPendingReviews() {
        return ResponseEntity.ok(reviewService.getPendingReviews());
    }

    @GetMapping("/approved")
    public ResponseEntity<List<ReviewDto>> getApprovedReviews() {
        return ResponseEntity.ok(reviewService.getApprovedReviews());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ReviewDto>> getRecentReviews() {
        return ResponseEntity.ok(reviewService.getRecentApprovedReviews());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ReviewDto> approveReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.approveReview(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ReviewDto> rejectReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.rejectReview(id));
    }

    @PostMapping("/{id}/response")
    public ResponseEntity<ReviewDto> addAdminResponse(@PathVariable Long id, @RequestParam String response) {
        return ResponseEntity.ok(reviewService.addAdminResponse(id, response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
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

    @GetMapping("/overall-rating")
    public ResponseEntity<Map<String, Object>> getOverallRating() {
        Double avgRating = reviewService.getOverallAverageRating();
        return ResponseEntity.ok(Map.of("overallRating", avgRating));
    }
}
