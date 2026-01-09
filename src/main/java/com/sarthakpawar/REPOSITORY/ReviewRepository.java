package com.sarthakpawar.REPOSITORY;

import com.sarthakpawar.ENTITY.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRoomId(Long roomId);

    List<Review> findByUserId(Long userId);

    Optional<Review> findByReservationId(Long reservationId);

    List<Review> findByIsApprovedTrue();

    List<Review> findByIsApprovedFalse();

    List<Review> findByRoomIdAndIsApprovedTrue(Long roomId);

    @Query("SELECT AVG(r.overallRating) FROM Review r WHERE r.room.id = :roomId AND r.isApproved = true")
    Double getAverageRatingByRoom(@Param("roomId") Long roomId);

    @Query("SELECT AVG(r.overallRating) FROM Review r WHERE r.isApproved = true")
    Double getOverallAverageRating();

    @Query("SELECT AVG(r.cleanlinessRating) FROM Review r WHERE r.room.id = :roomId AND r.isApproved = true")
    Double getAverageCleanlinessRating(@Param("roomId") Long roomId);

    @Query("SELECT AVG(r.serviceRating) FROM Review r WHERE r.room.id = :roomId AND r.isApproved = true")
    Double getAverageServiceRating(@Param("roomId") Long roomId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.room.id = :roomId AND r.isApproved = true")
    Long getReviewCountByRoom(@Param("roomId") Long roomId);

    @Query("SELECT r FROM Review r WHERE r.isApproved = true ORDER BY r.createdAt DESC")
    List<Review> findRecentApprovedReviews();

    @Query("SELECT r FROM Review r WHERE r.room.id = :roomId AND r.isApproved = true ORDER BY r.overallRating DESC")
    List<Review> findTopRatedReviewsByRoom(@Param("roomId") Long roomId);
}
