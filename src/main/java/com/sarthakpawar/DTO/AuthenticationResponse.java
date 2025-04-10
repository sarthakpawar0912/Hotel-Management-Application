package com.sarthakpawar.DTO;

import com.sarthakpawar.ENUMS.UserRole;
import lombok.Data;

@Data
public class AuthenticationResponse {

    private String jwt;

    private Long userId;

    private UserRole userRole;

}
