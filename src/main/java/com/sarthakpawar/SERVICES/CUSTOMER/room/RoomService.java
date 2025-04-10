package com.sarthakpawar.SERVICES.CUSTOMER.room;

import com.sarthakpawar.DTO.RoomsResponseDto;

public interface RoomService {

    RoomsResponseDto getAvailableRooms(int pageNumber);

}
