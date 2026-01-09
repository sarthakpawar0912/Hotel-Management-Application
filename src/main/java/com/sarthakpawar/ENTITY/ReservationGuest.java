package com.sarthakpawar.ENTITY;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"reservation_id", "guest_id"}))
public class ReservationGuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Guest guest;

    private Boolean isPrimary = false; // Primary guest for the booking
}
