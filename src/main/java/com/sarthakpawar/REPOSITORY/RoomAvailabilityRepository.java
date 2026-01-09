package com.sarthakpawar.REPOSITORY;

import com.sarthakpawar.ENTITY.Room;
import com.sarthakpawar.ENTITY.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    List<RoomAvailability> findByRoom(Room room);

    List<RoomAvailability> findByRoomId(Long roomId);

    Optional<RoomAvailability> findByRoomAndDate(Room room, LocalDate date);

    Optional<RoomAvailability> findByRoomIdAndDate(Long roomId, LocalDate date);

    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.room.id = :roomId AND ra.date BETWEEN :startDate AND :endDate")
    List<RoomAvailability> findByRoomIdAndDateRange(@Param("roomId") Long roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.date BETWEEN :startDate AND :endDate")
    List<RoomAvailability> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.room.id = :roomId AND ra.date BETWEEN :startDate AND :endDate AND ra.isAvailable = true AND ra.isBlocked = false")
    List<RoomAvailability> findAvailableDatesByRoom(@Param("roomId") Long roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.date = :date AND ra.isAvailable = true AND ra.isBlocked = false")
    List<RoomAvailability> findAvailableRoomsByDate(@Param("date") LocalDate date);

    @Query("SELECT DISTINCT ra.room FROM RoomAvailability ra WHERE ra.date BETWEEN :startDate AND :endDate AND ra.isAvailable = true AND ra.isBlocked = false GROUP BY ra.room HAVING COUNT(ra) = :days")
    List<Room> findRoomsAvailableForDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("days") Long days);

    @Modifying
    @Query("UPDATE RoomAvailability ra SET ra.isAvailable = :available WHERE ra.room.id = :roomId AND ra.date BETWEEN :startDate AND :endDate")
    void updateAvailabilityForRange(@Param("roomId") Long roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("available") Boolean available);

    @Modifying
    @Query("UPDATE RoomAvailability ra SET ra.isBlocked = :blocked, ra.blockReason = :reason WHERE ra.room.id = :roomId AND ra.date BETWEEN :startDate AND :endDate")
    void blockRoomForRange(@Param("roomId") Long roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("blocked") Boolean blocked, @Param("reason") String reason);

    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.isBlocked = true")
    List<RoomAvailability> findAllBlockedDates();

    List<RoomAvailability> findByReservationId(Long reservationId);
}
