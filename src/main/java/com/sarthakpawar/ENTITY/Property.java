package com.sarthakpawar.ENTITY;

import com.sarthakpawar.DTO.PropertyDto;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Base64;

@Entity
@Data
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    private String phone;
    private String email;
    private String website;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] logo;

    private String checkInTime;
    private String checkOutTime;

    @Column(length = 2000)
    private String cancellationPolicy;

    @Column(length = 2000)
    private String termsAndConditions;

    @Column(length = 1000)
    private String amenities;

    private Double latitude;
    private Double longitude;

    private Integer starRating;

    private Boolean isActive = true;

    private Integer totalRooms;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public PropertyDto getPropertyDto() {
        PropertyDto dto = new PropertyDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAddress(address);
        dto.setCity(city);
        dto.setState(state);
        dto.setCountry(country);
        dto.setZipCode(zipCode);
        dto.setPhone(phone);
        dto.setEmail(email);
        dto.setWebsite(website);
        dto.setCheckInTime(checkInTime);
        dto.setCheckOutTime(checkOutTime);
        dto.setCancellationPolicy(cancellationPolicy);
        dto.setTermsAndConditions(termsAndConditions);
        dto.setAmenities(amenities);
        dto.setLatitude(latitude);
        dto.setLongitude(longitude);
        dto.setStarRating(starRating);
        dto.setIsActive(isActive);
        dto.setTotalRooms(totalRooms);
        if (logo != null && logo.length > 0) {
            dto.setReturnedLogo(Base64.getEncoder().encodeToString(logo));
        }
        return dto;
    }
}
