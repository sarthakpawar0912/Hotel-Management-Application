package com.sarthakpawar.REPOSITORY;

import com.sarthakpawar.ENTITY.Promotion;
import com.sarthakpawar.ENTITY.PromotionUsage;
import com.sarthakpawar.ENTITY.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionUsageRepository extends JpaRepository<PromotionUsage, Long> {

    List<PromotionUsage> findByPromotion(Promotion promotion);

    List<PromotionUsage> findByPromotionId(Long promotionId);

    List<PromotionUsage> findByUser(User user);

    List<PromotionUsage> findByUserId(Long userId);

    List<PromotionUsage> findByReservationId(Long reservationId);

    @Query("SELECT COUNT(pu) FROM PromotionUsage pu WHERE pu.promotion.id = :promotionId AND pu.user.id = :userId")
    Long countUserPromotionUsage(@Param("promotionId") Long promotionId, @Param("userId") Long userId);

    @Query("SELECT COUNT(pu) FROM PromotionUsage pu WHERE pu.promotion.id = :promotionId")
    Long countTotalPromotionUsage(@Param("promotionId") Long promotionId);

    @Query("SELECT SUM(pu.discountApplied) FROM PromotionUsage pu WHERE pu.promotion.id = :promotionId")
    Long getTotalDiscountByPromotion(@Param("promotionId") Long promotionId);
}
