package com.sarthakpawar.DTO;

import lombok.Data;

import java.util.List;

@Data
public class RoomsResponseDto {

    private List<RoomDto> roomDtoList;
    private Integer totalPages;
    private Integer pageNumber;
    private Long totalElements;

    // For filter options
    private List<String> roomTypes;
    private Long minPrice;
    private Long maxPrice;

    public RoomsResponseDto(List<RoomDto> roomDtoList, Integer totalPages, Integer pageNumber) {
        this.roomDtoList = roomDtoList;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
    }

    public RoomsResponseDto() {
    }

}
