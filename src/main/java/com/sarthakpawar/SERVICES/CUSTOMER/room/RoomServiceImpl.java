package com.sarthakpawar.SERVICES.CUSTOMER.room;

import com.sarthakpawar.DTO.RoomsResponseDto;
import com.sarthakpawar.ENTITY.Room;
import com.sarthakpawar.REPOSITORY.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service("customerRoomService")
public class RoomServiceImpl implements RoomService {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private RoomRepository roomRepository;

    public RoomsResponseDto getAvailableRooms(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by("id").descending());
        Page<Room> roomPage = roomRepository.findByAvailable(true, pageable);

        RoomsResponseDto roomsResponseDto = new RoomsResponseDto();
        roomsResponseDto.setPageNumber(roomPage.getPageable().getPageNumber());
        roomsResponseDto.setTotalPages(roomPage.getTotalPages());
        roomsResponseDto.setTotalElements(roomPage.getTotalElements());
        roomsResponseDto.setRoomDtoList(roomPage.stream().map(Room::getRoomDto).collect(Collectors.toList()));
        return roomsResponseDto;
    }

    // Search and filter available rooms
    public RoomsResponseDto searchAvailableRooms(String name, String type, Long minPrice,
                                                  Long maxPrice, Integer capacity, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by("id").descending());

        Page<Room> roomPage = roomRepository.searchAvailableRooms(
                name, type, minPrice, maxPrice, capacity, pageable
        );

        RoomsResponseDto roomsResponseDto = new RoomsResponseDto();
        roomsResponseDto.setPageNumber(roomPage.getNumber());
        roomsResponseDto.setTotalPages(roomPage.getTotalPages());
        roomsResponseDto.setTotalElements(roomPage.getTotalElements());
        roomsResponseDto.setRoomDtoList(roomPage.getContent().stream()
                .map(Room::getRoomDto)
                .collect(Collectors.toList()));

        return roomsResponseDto;
    }

}
