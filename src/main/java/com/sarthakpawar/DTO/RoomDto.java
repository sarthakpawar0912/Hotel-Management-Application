package com.sarthakpawar.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RoomDto {

    private Long id;

    private String name;

    private String type;

    private Long price;

    private boolean available;

    // Base64 encoded image string (for response)
    private String image;

    // Room description
    private String description;

    // Room capacity (number of guests)
    private Integer capacity;

    // Room floor number
    private Integer floorNumber;

    // Room number/code
    private String roomNumber;

}
