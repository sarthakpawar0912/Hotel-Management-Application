package com.sarthakpawar.DTO;

import lombok.Data;

@Data
public class RoomDto {

    private Long id;
    private String name;
    private String type;
    private Long price;
    private boolean available;

    public RoomDto(Long id, String name, String type, Long price, boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.available = available;
    }

    public RoomDto() {
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

    @Override
    public String toString() {
        return "RoomDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", available=" + available +
                '}';
    }
}
