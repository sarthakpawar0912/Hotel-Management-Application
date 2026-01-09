package com.sarthakpawar.REPOSITORY;

import com.sarthakpawar.ENTITY.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByPromoCode(String promoCode);

    List<Promotion> findByIsActiveTrue();

    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND " +
           "(p.startDate IS NULL OR p.startDate <= :date) AND " +
           "(p.endDate IS NULL OR p.endDate >= :date) AND " +
           "(p.usageLimit IS NULL OR p.usageCount < p.usageLimit)")
    List<Promotion> findValidPromotions(@Param("date") LocalDate date);

    @Query("SELECT p FROM Promotion p WHERE p.promoCode = :code AND p.isActive = true AND " +
           "(p.startDate IS NULL OR p.startDate <= :date) AND " +
           "(p.endDate IS NULL OR p.endDate >= :date) AND " +
           "(p.usageLimit IS NULL OR p.usageCount < p.usageLimit)")
    Optional<Promotion> findValidPromoCode(@Param("code") String code, @Param("date") LocalDate date);

    @Query("SELECT p FROM Promotion p WHERE p.endDate < :date")
    List<Promotion> findExpiredPromotions(@Param("date") LocalDate date);

    @Query("SELECT p FROM Promotion p WHERE p.startDate > :date")
    List<Promotion> findUpcomingPromotions(@Param("date") LocalDate date);

    List<Promotion> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
}
