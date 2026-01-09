package com.sarthakpawar.SERVICES.CUSTOMER.room;

import com.sarthakpawar.DTO.RoomsResponseDto;

public interface RoomService {

    RoomsResponseDto getAvailableRooms(int pageNumber);

    // Search available rooms with filters
    RoomsResponseDto searchAvailableRooms(String name, String type, Long minPrice,
                                           Long maxPrice, Integer capacity, int pageNumber);

}
