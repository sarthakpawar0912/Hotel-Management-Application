package com.sarthakpawar.DTO;

import java.util.List;

public class RoomsResponseDto {

    private List<RoomDto> roomDtoList;
    private Integer totalPages;
    private Integer pageNumber;


    public RoomsResponseDto(List<RoomDto> roomDtoList, Integer totalPages, Integer pageNumber) {
        this.roomDtoList = roomDtoList;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
    }

    public RoomsResponseDto() {
    }

    @Override
    public String toString() {
        return "RoomsResponseDto{" +
                "roomDtoList=" + roomDtoList +
                ", totalPages=" + totalPages +
                ", pageNumber=" + pageNumber +
                '}';
    }

    public List<RoomDto> getRoomDtoList() {
        return roomDtoList;
    }

    public void setRoomDtoList(List<RoomDto> roomDtoList) {
        this.roomDtoList = roomDtoList;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}
