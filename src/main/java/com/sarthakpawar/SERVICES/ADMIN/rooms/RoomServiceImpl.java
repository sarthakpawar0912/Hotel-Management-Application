package com.sarthakpawar.SERVICES.ADMIN.rooms;

import com.sarthakpawar.DTO.RoomDto;
import com.sarthakpawar.DTO.RoomsResponseDto;
import com.sarthakpawar.ENTITY.Room;
import com.sarthakpawar.REPOSITORY.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("adminRoomService")
public class RoomServiceImpl implements RoomService {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private RoomRepository roomRepository;

    public boolean postRoom(RoomDto roomDto, MultipartFile imageFile) throws IOException {
        try {
            Room room = new Room();

            room.setName(roomDto.getName());
            room.setPrice(roomDto.getPrice());
            room.setType(roomDto.getType());
            room.setDescription(roomDto.getDescription());
            room.setCapacity(roomDto.getCapacity());
            room.setFloorNumber(roomDto.getFloorNumber());
            room.setRoomNumber(roomDto.getRoomNumber());
            room.setAvailable(true);

            // Handle image upload
            if (imageFile != null && !imageFile.isEmpty()) {
                room.setImage(imageFile.getBytes());
            }

            roomRepository.save(room);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public RoomsResponseDto getAllRooms(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by("id").descending());
        Page<Room> roomPage = roomRepository.findAll(pageable);

        RoomsResponseDto roomsResponseDto = new RoomsResponseDto();
        roomsResponseDto.setPageNumber(roomPage.getNumber());
        roomsResponseDto.setTotalPages(roomPage.getTotalPages());
        roomsResponseDto.setTotalElements(roomPage.getTotalElements());
        roomsResponseDto.setRoomDtoList(roomPage.getContent().stream()
                .map(Room::getRoomDto)
                .collect(Collectors.toList()));

        return roomsResponseDto;
    }

    public RoomDto getRoomById(Long id){
        Optional<Room> optionalRoom=roomRepository.findById(id);
        if(optionalRoom.isPresent()) {
            return optionalRoom.get().getRoomDto();
        }else {
            throw new EntityNotFoundException("Room not present.");
        }
    }

    public boolean updateRoom(Long id, RoomDto roomDto, MultipartFile imageFile) throws IOException {
        Optional<Room> optionalRoom=roomRepository.findById(id);
        if(optionalRoom.isPresent()){
            Room existingRoom=optionalRoom.get();
            existingRoom.setName(roomDto.getName());
            existingRoom.setPrice(roomDto.getPrice());
            existingRoom.setType(roomDto.getType());
            existingRoom.setDescription(roomDto.getDescription());
            existingRoom.setCapacity(roomDto.getCapacity());
            existingRoom.setFloorNumber(roomDto.getFloorNumber());
            existingRoom.setRoomNumber(roomDto.getRoomNumber());

            // Handle image upload (only update if new image provided)
            if (imageFile != null && !imageFile.isEmpty()) {
                existingRoom.setImage(imageFile.getBytes());
            }

            roomRepository.save(existingRoom);
            return true;
        }
        return false;
    }

    public void deleteRoom(Long id){
        Optional<Room> optionalRoom=roomRepository.findById(id);
        if(optionalRoom.isPresent()) {
            roomRepository.deleteById(id);
        }else {
            throw  new EntityNotFoundException("Room not present.");
        }
    }

    // Search and filter rooms
    public RoomsResponseDto searchRooms(String name, String type, Long minPrice, Long maxPrice,
                                         Integer capacity, Boolean available, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by("id").descending());

        Page<Room> roomPage = roomRepository.searchAndFilterRooms(
                name, type, minPrice, maxPrice, capacity, available, pageable
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
