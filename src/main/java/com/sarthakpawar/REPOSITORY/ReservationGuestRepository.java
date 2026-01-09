package com.sarthakpawar.REPOSITORY;

import com.sarthakpawar.ENTITY.Guest;
import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.ENTITY.ReservationGuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationGuestRepository extends JpaRepository<ReservationGuest, Long> {

    List<ReservationGuest> findByReservation(Reservation reservation);

    List<ReservationGuest> findByReservationId(Long reservationId);

    List<ReservationGuest> findByGuest(Guest guest);

    List<ReservationGuest> findByGuestId(Long guestId);

    Optional<ReservationGuest> findByReservationAndGuest(Reservation reservation, Guest guest);

    Optional<ReservationGuest> findByReservationIdAndGuestId(Long reservationId, Long guestId);

    @Query("SELECT rg FROM ReservationGuest rg WHERE rg.reservation.id = :reservationId AND rg.isPrimary = true")
    Optional<ReservationGuest> findPrimaryGuestByReservation(@Param("reservationId") Long reservationId);

    @Query("SELECT COUNT(rg) FROM ReservationGuest rg WHERE rg.reservation.id = :reservationId")
    Long countGuestsByReservation(@Param("reservationId") Long reservationId);
}
