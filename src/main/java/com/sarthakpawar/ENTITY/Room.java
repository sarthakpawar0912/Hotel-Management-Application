package com.sarthakpawar.ENTITY;


import com.sarthakpawar.DTO.RoomDto;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Base64;

@Entity
@Data
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type;

    private Long price;

    private boolean available;

    // Room Image stored as byte array
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    // Room description for better details
    @Column(length = 1000)
    private String description;

    // Room capacity (number of guests)
    private Integer capacity;

    // Room floor number
    private Integer floorNumber;

    // Room number/code
    private String roomNumber;


    public RoomDto getRoomDto() {
        RoomDto roomDto = new RoomDto();

        roomDto.setId(id);
        roomDto.setName(name);
        roomDto.setType(type);
        roomDto.setAvailable(available);
        roomDto.setPrice(price);
        roomDto.setDescription(description);
        roomDto.setCapacity(capacity);
        roomDto.setFloorNumber(floorNumber);
        roomDto.setRoomNumber(roomNumber);

        // Convert byte array to Base64 string for frontend
        if (image != null && image.length > 0) {
            roomDto.setImage(Base64.getEncoder().encodeToString(image));
        }

        return roomDto;
    }

}
