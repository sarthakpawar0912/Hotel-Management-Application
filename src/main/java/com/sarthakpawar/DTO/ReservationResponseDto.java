package com.sarthakpawar.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ReservationResponseDto {


    private Integer totalPages;
    private Integer pageNumber;
    private List<ReservationDto> reservationDtoList;
    public ReservationResponseDto() {
    }

    @Override
    public String toString() {
        return "ReservationResponseDto{" +
                "totalPages=" + totalPages +
                ", pageNumber=" + pageNumber +
                ", reservationDtoList=" + reservationDtoList +
                '}';
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

    public List<ReservationDto> getReservationDtoList() {
        return reservationDtoList;
    }

    public void setReservationDtoList(List<ReservationDto> reservationDtoList) {
        this.reservationDtoList = reservationDtoList;
    }

    public ReservationResponseDto(Integer totalPages, Integer pageNumber, List<ReservationDto> reservationDtoList) {
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
        this.reservationDtoList = reservationDtoList;
    }
}
