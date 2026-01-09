package com.sarthakpawar.SERVICES.ADMIN.rooms;

import com.sarthakpawar.DTO.RoomDto;
import com.sarthakpawar.DTO.RoomsResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface RoomService {

    boolean postRoom(RoomDto roomDto, MultipartFile imageFile) throws IOException;

    RoomsResponseDto getAllRooms(int pageNumber);

    RoomDto getRoomById(Long id);

    boolean updateRoom(Long id, RoomDto roomDto, MultipartFile imageFile) throws IOException;

    void deleteRoom(Long id);

    // Search and Filter methods
    RoomsResponseDto searchRooms(String name, String type, Long minPrice, Long maxPrice,
                                  Integer capacity, Boolean available, int pageNumber);

}
