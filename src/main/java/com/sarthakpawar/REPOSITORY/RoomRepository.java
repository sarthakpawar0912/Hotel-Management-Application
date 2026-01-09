package com.sarthakpawar.REPOSITORY;

import com.sarthakpawar.ENTITY.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {

    Page<Room> findByAvailable(boolean available, Pageable pageable);

    // Search by name (case-insensitive, partial match)
    Page<Room> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Search by type (case-insensitive, partial match)
    Page<Room> findByTypeContainingIgnoreCase(String type, Pageable pageable);

    // Filter by price range
    Page<Room> findByPriceBetween(Long minPrice, Long maxPrice, Pageable pageable);

    // Filter by capacity
    Page<Room> findByCapacityGreaterThanEqual(Integer capacity, Pageable pageable);

    // Combined search and filter with dynamic query
    @Query("SELECT r FROM Room r WHERE " +
           "(:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:type IS NULL OR LOWER(r.type) LIKE LOWER(CONCAT('%', :type, '%'))) AND " +
           "(:minPrice IS NULL OR r.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR r.price <= :maxPrice) AND " +
           "(:capacity IS NULL OR r.capacity >= :capacity) AND " +
           "(:available IS NULL OR r.available = :available)")
    Page<Room> searchAndFilterRooms(
            @Param("name") String name,
            @Param("type") String type,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("capacity") Integer capacity,
            @Param("available") Boolean available,
            Pageable pageable
    );

    // Search available rooms with filters (for customers)
    @Query("SELECT r FROM Room r WHERE r.available = true AND " +
           "(:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:type IS NULL OR LOWER(r.type) LIKE LOWER(CONCAT('%', :type, '%'))) AND " +
           "(:minPrice IS NULL OR r.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR r.price <= :maxPrice) AND " +
           "(:capacity IS NULL OR r.capacity >= :capacity)")
    Page<Room> searchAvailableRooms(
            @Param("name") String name,
            @Param("type") String type,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("capacity") Integer capacity,
            Pageable pageable
    );

    // Get distinct room types for filter dropdown
    @Query("SELECT DISTINCT r.type FROM Room r WHERE r.type IS NOT NULL")
    List<String> findDistinctTypes();

    // Get min and max prices for filter slider
    @Query("SELECT MIN(r.price), MAX(r.price) FROM Room r")
    Object[] findPriceRange();

}
