package com.sarthakpawar.ENTITY;

import com.sarthakpawar.DTO.GuestDto;
import com.sarthakpawar.ENUMS.IdProofType;
import com.sarthakpawar.ENUMS.LoyaltyTier;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String alternatePhone;

    private LocalDate dateOfBirth;

    private String gender;

    private String nationality;

    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    @Enumerated(EnumType.STRING)
    private IdProofType idProofType;

    private String idProofNumber;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] idProofImage;

    private String companyName;
    private String gstin;

    // Loyalty Points System
    private Integer loyaltyPoints = 0;

    @Enumerated(EnumType.STRING)
    private LoyaltyTier loyaltyTier = LoyaltyTier.BRONZE;

    // Preferences
    @Column(length = 1000)
    private String preferences;

    @Column(length = 1000)
    private String specialRequests;

    private Boolean isVip = false;
    private Boolean isBlacklisted = false;
    private String blacklistReason;

    // Guest history
    private Integer totalStays = 0;
    private Long totalSpent = 0L;
    private LocalDateTime lastStayDate;

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

    public GuestDto getGuestDto() {
        GuestDto dto = new GuestDto();
        dto.setId(id);
        if (user != null) {
            dto.setUserId(user.getId());
        }
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setAlternatePhone(alternatePhone);
        dto.setDateOfBirth(dateOfBirth);
        dto.setGender(gender);
        dto.setNationality(nationality);
        dto.setAddress(address);
        dto.setCity(city);
        dto.setState(state);
        dto.setCountry(country);
        dto.setZipCode(zipCode);
        dto.setIdProofType(idProofType);
        dto.setIdProofNumber(idProofNumber);
        dto.setCompanyName(companyName);
        dto.setGstin(gstin);
        dto.setLoyaltyPoints(loyaltyPoints);
        dto.setLoyaltyTier(loyaltyTier);
        dto.setPreferences(preferences);
        dto.setSpecialRequests(specialRequests);
        dto.setIsVip(isVip);
        dto.setIsBlacklisted(isBlacklisted);
        dto.setBlacklistReason(blacklistReason);
        dto.setTotalStays(totalStays);
        dto.setTotalSpent(totalSpent);
        dto.setLastStayDate(lastStayDate);
        dto.setCreatedAt(createdAt);
        return dto;
    }

    public String getFullName() {
        return firstName + (lastName != null ? " " + lastName : "");
    }
}
