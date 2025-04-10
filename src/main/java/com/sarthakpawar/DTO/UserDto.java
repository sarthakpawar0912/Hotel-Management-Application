package com.sarthakpawar.DTO;

import com.sarthakpawar.ENUMS.UserRole;
import lombok.Data;

@Data
public class UserDto {

    private Long id;

    private String email;

    private String name;

    private UserRole userRole;

}
