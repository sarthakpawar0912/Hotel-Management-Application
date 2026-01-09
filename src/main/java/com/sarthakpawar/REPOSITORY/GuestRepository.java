package com.sarthakpawar.REPOSITORY;

import com.sarthakpawar.ENTITY.Guest;
import com.sarthakpawar.ENTITY.User;
import com.sarthakpawar.ENUMS.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {

    Optional<Guest> findByUser(User user);

    Optional<Guest> findByUserId(Long userId);

    Optional<Guest> findByEmail(String email);

    Optional<Guest> findByPhone(String phone);

    Optional<Guest> findByIdProofNumber(String idProofNumber);

    List<Guest> findByIsVipTrue();

    List<Guest> findByIsBlacklistedTrue();

    List<Guest> findByLoyaltyTier(LoyaltyTier tier);

    @Query("SELECT g FROM Guest g WHERE g.loyaltyPoints >= :minPoints")
    List<Guest> findByMinLoyaltyPoints(@Param("minPoints") Integer minPoints);

    @Query("SELECT g FROM Guest g WHERE LOWER(g.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(g.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Guest> searchByName(@Param("name") String name);

    @Query("SELECT g FROM Guest g ORDER BY g.totalSpent DESC")
    List<Guest> findTopSpenders();

    @Query("SELECT g FROM Guest g ORDER BY g.totalStays DESC")
    List<Guest> findFrequentGuests();
}
