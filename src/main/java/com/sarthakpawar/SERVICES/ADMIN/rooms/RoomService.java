package com.sarthakpawar.SERVICES.ADMIN.rooms;

import com.sarthakpawar.DTO.RoomDto;
import com.sarthakpawar.DTO.RoomsResponseDto;

public interface RoomService {

    boolean postRoom(RoomDto roomDto);

    RoomsResponseDto getAllRooms(int pageNumber);

    RoomDto getRoomById(Long id);

    boolean updateRoom(Long id,RoomDto roomDto);

    void deleteRoom(Long id);

}
