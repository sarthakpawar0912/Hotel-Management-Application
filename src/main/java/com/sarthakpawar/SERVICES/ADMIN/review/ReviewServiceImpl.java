package com.sarthakpawar.SERVICES.ADMIN.review;

import com.sarthakpawar.DTO.ReviewDto;
import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.ENTITY.Review;
import com.sarthakpawar.ENTITY.Room;
import com.sarthakpawar.ENTITY.User;
import com.sarthakpawar.ENUMS.ReservationStatus;
import com.sarthakpawar.REPOSITORY.ReservationRepository;
import com.sarthakpawar.REPOSITORY.ReviewRepository;
import com.sarthakpawar.REPOSITORY.RoomRepository;
import com.sarthakpawar.REPOSITORY.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewDto createReview(ReviewDto reviewDto) {
        // Check if review already exists for this reservation
        if (reviewDto.getReservationId() != null &&
            reviewRepository.findByReservationId(reviewDto.getReservationId()).isPresent()) {
            throw new RuntimeException("Review already exists for this reservation");
        }

        Review review = new Review();
        mapDtoToEntity(reviewDto, review);

        if (reviewDto.getReservationId() != null) {
            Reservation reservation = reservationRepository.findById(reviewDto.getReservationId())
                    .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

            // Verify reservation is checked out
            if (reservation.getReservationStatus() != ReservationStatus.CHECKED_OUT) {
                throw new RuntimeException("Can only review after checkout");
            }

            review.setReservation(reservation);
            review.setRoom(reservation.getRoom());
            review.setUser(reservation.getUser());
            review.setIsVerifiedStay(true);
        } else {
            if (reviewDto.getRoomId() != null) {
                Room room = roomRepository.findById(reviewDto.getRoomId())
                        .orElseThrow(() -> new EntityNotFoundException("Room not found"));
                review.setRoom(room);
            }
            if (reviewDto.getUserId() != null) {
                User user = userRepository.findById(reviewDto.getUserId())
                        .orElseThrow(() -> new EntityNotFoundException("User not found"));
                review.setUser(user);
            }
            review.setIsVerifiedStay(false);
        }

        review.setIsApproved(false); // Needs admin approval

        Review savedReview = reviewRepository.save(review);
        return savedReview.getReviewDto();
    }

    @Override
    @Transactional
    public ReviewDto updateReview(Long id, ReviewDto reviewDto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        review.setOverallRating(reviewDto.getOverallRating());
        review.setCleanlinessRating(reviewDto.getCleanlinessRating());
        review.setServiceRating(reviewDto.getServiceRating());
        review.setAmenitiesRating(reviewDto.getAmenitiesRating());
        review.setValueForMoneyRating(reviewDto.getValueForMoneyRating());
        review.setLocationRating(reviewDto.getLocationRating());
        review.setTitle(reviewDto.getTitle());
        review.setComment(reviewDto.getComment());
        review.setPros(reviewDto.getPros());
        review.setCons(reviewDto.getCons());
        review.setIsAnonymous(reviewDto.getIsAnonymous());

        Review updatedReview = reviewRepository.save(review);
        return updatedReview.getReviewDto();
    }

    @Override
    public ReviewDto getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return review.getReviewDto();
    }

    @Override
    public ReviewDto getReviewByReservation(Long reservationId) {
        Review review = reviewRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found for reservation"));
        return review.getReviewDto();
    }

    @Override
    public List<ReviewDto> getReviewsByRoom(Long roomId) {
        return reviewRepository.findByRoomId(roomId).stream()
                .map(Review::getReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(Review::getReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> getApprovedReviews() {
        return reviewRepository.findByIsApprovedTrue().stream()
                .map(Review::getReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> getPendingReviews() {
        return reviewRepository.findByIsApprovedFalse().stream()
                .map(Review::getReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> getApprovedReviewsByRoom(Long roomId) {
        return reviewRepository.findByRoomIdAndIsApprovedTrue(roomId).stream()
                .map(Review::getReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(Review::getReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewDto approveReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        review.setIsApproved(true);
        Review updatedReview = reviewRepository.save(review);
        return updatedReview.getReviewDto();
    }

    @Override
    @Transactional
    public ReviewDto rejectReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        review.setIsApproved(false);
        Review updatedReview = reviewRepository.save(review);
        return updatedReview.getReviewDto();
    }

    @Override
    @Transactional
    public ReviewDto addAdminResponse(Long id, String response) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        review.setAdminResponse(response);
        review.setAdminResponseAt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);
        return updatedReview.getReviewDto();
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found");
        }
        reviewRepository.deleteById(id);
    }

    @Override
    public Double getAverageRatingByRoom(Long roomId) {
        Double avg = reviewRepository.getAverageRatingByRoom(roomId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    @Override
    public Double getOverallAverageRating() {
        Double avg = reviewRepository.getOverallAverageRating();
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    @Override
    public Map<String, Double> getRatingBreakdownByRoom(Long roomId) {
        Map<String, Double> breakdown = new HashMap<>();

        Double cleanliness = reviewRepository.getAverageCleanlinessRating(roomId);
        Double service = reviewRepository.getAverageServiceRating(roomId);
        Double overall = reviewRepository.getAverageRatingByRoom(roomId);

        breakdown.put("overall", overall != null ? Math.round(overall * 10.0) / 10.0 : 0.0);
        breakdown.put("cleanliness", cleanliness != null ? Math.round(cleanliness * 10.0) / 10.0 : 0.0);
        breakdown.put("service", service != null ? Math.round(service * 10.0) / 10.0 : 0.0);

        return breakdown;
    }

    @Override
    public Long getReviewCountByRoom(Long roomId) {
        return reviewRepository.getReviewCountByRoom(roomId);
    }

    @Override
    public List<ReviewDto> getRecentApprovedReviews() {
        return reviewRepository.findRecentApprovedReviews().stream()
                .limit(10)
                .map(Review::getReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> getTopRatedReviewsByRoom(Long roomId) {
        return reviewRepository.findTopRatedReviewsByRoom(roomId).stream()
                .limit(5)
                .map(Review::getReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean canUserReviewRoom(Long userId, Long roomId) {
        // Check if user has any completed/checked-out reservation for this room
        // that hasn't been reviewed yet
        List<Reservation> completedReservations = reservationRepository
                .findByUserIdAndRoomIdAndReservationStatus(userId, roomId, ReservationStatus.CHECKED_OUT);

        for (Reservation reservation : completedReservations) {
            if (reviewRepository.findByReservationId(reservation.getId()).isEmpty()) {
                return true; // Has unreviewed completed stay
            }
        }
        return false;
    }

    private void mapDtoToEntity(ReviewDto dto, Review entity) {
        entity.setOverallRating(dto.getOverallRating());
        entity.setCleanlinessRating(dto.getCleanlinessRating());
        entity.setServiceRating(dto.getServiceRating());
        entity.setAmenitiesRating(dto.getAmenitiesRating());
        entity.setValueForMoneyRating(dto.getValueForMoneyRating());
        entity.setLocationRating(dto.getLocationRating());
        entity.setTitle(dto.getTitle());
        entity.setComment(dto.getComment());
        entity.setPros(dto.getPros());
        entity.setCons(dto.getCons());
        entity.setIsAnonymous(dto.getIsAnonymous() != null ? dto.getIsAnonymous() : false);
    }
}
