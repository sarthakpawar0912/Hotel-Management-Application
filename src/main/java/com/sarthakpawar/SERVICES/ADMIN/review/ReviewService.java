package com.sarthakpawar.SERVICES.ADMIN.review;

import com.sarthakpawar.DTO.ReviewDto;

import java.util.List;
import java.util.Map;

public interface ReviewService {

    ReviewDto createReview(ReviewDto reviewDto);

    ReviewDto updateReview(Long id, ReviewDto reviewDto);

    ReviewDto getReviewById(Long id);

    ReviewDto getReviewByReservation(Long reservationId);

    List<ReviewDto> getReviewsByRoom(Long roomId);

    List<ReviewDto> getReviewsByUser(Long userId);

    List<ReviewDto> getApprovedReviews();

    List<ReviewDto> getPendingReviews();

    List<ReviewDto> getApprovedReviewsByRoom(Long roomId);

    List<ReviewDto> getAllReviews();

    ReviewDto approveReview(Long id);

    ReviewDto rejectReview(Long id);

    ReviewDto addAdminResponse(Long id, String response);

    void deleteReview(Long id);

    // Statistics
    Double getAverageRatingByRoom(Long roomId);

    Double getOverallAverageRating();

    Map<String, Double> getRatingBreakdownByRoom(Long roomId);

    Long getReviewCountByRoom(Long roomId);

    List<ReviewDto> getRecentApprovedReviews();

    List<ReviewDto> getTopRatedReviewsByRoom(Long roomId);

    boolean canUserReviewRoom(Long userId, Long roomId);
}
