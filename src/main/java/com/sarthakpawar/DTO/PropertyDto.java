package com.sarthakpawar.DTO;

import lombok.Data;

@Data
public class PropertyDto {

    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String phone;
    private String email;
    private String website;
    private byte[] logo;
    private String returnedLogo;
    private String checkInTime;
    private String checkOutTime;
    private String cancellationPolicy;
    private String termsAndConditions;
    private String amenities;
    private Double latitude;
    private Double longitude;
    private Integer starRating;
    private Boolean isActive;
    private Integer totalRooms;
}
