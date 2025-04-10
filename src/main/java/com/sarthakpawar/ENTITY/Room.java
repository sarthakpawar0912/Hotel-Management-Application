package com.sarthakpawar.ENTITY;


import com.sarthakpawar.DTO.RoomDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

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

    public Room(Long id, String name, String type, Long price, boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.available = available;
    }

    public Room() {

    }

    public RoomDto getRoomDto() {
        RoomDto roomDto = new RoomDto();

        roomDto.setId(id);
        roomDto.setName(name);
        roomDto.setType(type);
        roomDto.setAvailable(available);
        roomDto.setPrice(price);

        return roomDto;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", available=" + available +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
