package com.sarthakpawar.ENTITY;

import com.sarthakpawar.DTO.RoomAvailabilityDto;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "date"}))
public class RoomAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Room room;

    @Column(nullable = false)
    private LocalDate date;

    private Boolean isAvailable = true;
    private Boolean isBlocked = false;

    private String blockReason;

    // Dynamic pricing
    private Long customPrice;

    // Minimum stay requirement
    private Integer minStay;

    // Reservation reference if booked
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    public RoomAvailabilityDto getRoomAvailabilityDto() {
        RoomAvailabilityDto dto = new RoomAvailabilityDto();
        dto.setId(id);
        dto.setRoomId(room.getId());
        dto.setRoomName(room.getName());
        dto.setDate(date);
        dto.setIsAvailable(isAvailable);
        dto.setIsBlocked(isBlocked);
        dto.setBlockReason(blockReason);
        dto.setCustomPrice(customPrice != null ? customPrice : room.getPrice());
        dto.setMinStay(minStay);
        if (reservation != null) {
            dto.setReservationId(reservation.getId());
        }
        return dto;
    }
}
