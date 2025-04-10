package com.sarthakpawar.SERVICES.CUSTOMER.room;

import com.sarthakpawar.DTO.RoomsResponseDto;
import com.sarthakpawar.ENTITY.Room;
import com.sarthakpawar.REPOSITORY.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service("customerRoomService")
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public RoomsResponseDto getAvailableRooms(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 6);
        Page<Room> roomPage=roomRepository.findByAvailable(true,pageable);
        RoomsResponseDto roomsResponseDto=new RoomsResponseDto();
        roomsResponseDto.setPageNumber(roomPage.getPageable().getPageNumber());
        roomsResponseDto.setTotalPages(roomPage.getTotalPages());
        roomsResponseDto.setRoomDtoList(roomPage.stream().map(Room::getRoomDto).collect(Collectors.toList()));
        return roomsResponseDto;
    }

}
