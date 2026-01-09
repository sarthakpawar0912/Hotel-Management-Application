package com.sarthakpawar.DTO;

import com.sarthakpawar.ENUMS.IdProofType;
import com.sarthakpawar.ENUMS.LoyaltyTier;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GuestDto {

    private Long id;
    private Long userId;
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
    private IdProofType idProofType;
    private String idProofNumber;
    private byte[] idProofImage;
    private String returnedIdProofImage;
    private String companyName;
    private String gstin;
    private Integer loyaltyPoints;
    private LoyaltyTier loyaltyTier;
    private String preferences;
    private String specialRequests;
    private Boolean isVip;
    private Boolean isBlacklisted;
    private String blacklistReason;
    private Integer totalStays;
    private Long totalSpent;
    private LocalDateTime lastStayDate;
    private LocalDateTime createdAt;
}
