package com.sarthakpawar.CONTROLLER.ADMIN;

import com.sarthakpawar.DTO.RoomDto;
import com.sarthakpawar.DTO.RoomsResponseDto;
import com.sarthakpawar.REPOSITORY.RoomRepository;
import com.sarthakpawar.SERVICES.ADMIN.rooms.RoomService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    // Create room with image (multipart/form-data)
    @PostMapping(value = "/room", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postRoom(
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("price") Long price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "capacity", required = false) Integer capacity,
            @RequestParam(value = "floorNumber", required = false) Integer floorNumber,
            @RequestParam(value = "roomNumber", required = false) String roomNumber,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            RoomDto roomDto = new RoomDto();
            roomDto.setName(name);
            roomDto.setType(type);
            roomDto.setPrice(price);
            roomDto.setDescription(description);
            roomDto.setCapacity(capacity);
            roomDto.setFloorNumber(floorNumber);
            roomDto.setRoomNumber(roomNumber);

            boolean success = roomService.postRoom(roomDto, imageFile);
            if (success) {
                return ResponseEntity.status(HttpStatus.OK).body("Room created successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create room");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }

    @GetMapping("/rooms/{pageNumber}")
    public ResponseEntity<?> getAllRooms(@PathVariable int pageNumber){
        return ResponseEntity.ok(roomService.getAllRooms(pageNumber));
    }

    @GetMapping("/room/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(roomService.getRoomById(id));
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    // Update room with image (multipart/form-data)
    @PutMapping(value = "/room/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRoom(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("price") Long price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "capacity", required = false) Integer capacity,
            @RequestParam(value = "floorNumber", required = false) Integer floorNumber,
            @RequestParam(value = "roomNumber", required = false) String roomNumber,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            RoomDto roomDto = new RoomDto();
            roomDto.setName(name);
            roomDto.setType(type);
            roomDto.setPrice(price);
            roomDto.setDescription(description);
            roomDto.setCapacity(capacity);
            roomDto.setFloorNumber(floorNumber);
            roomDto.setRoomNumber(roomNumber);

            boolean success = roomService.updateRoom(id, roomDto, imageFile);
            if (success) {
                return ResponseEntity.status(HttpStatus.OK).body("Room updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }

    @DeleteMapping("room/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id){
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.ok(null);
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Search and filter rooms
    @GetMapping("/rooms/search")
    public ResponseEntity<?> searchRooms(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int pageNumber) {

        RoomsResponseDto response = roomService.searchRooms(
                name, type, minPrice, maxPrice, capacity, available, pageNumber
        );
        return ResponseEntity.ok(response);
    }

    // Get filter options (room types and price range)
    @GetMapping("/rooms/filter-options")
    public ResponseEntity<?> getFilterOptions() {
        List<String> roomTypes = roomRepository.findDistinctTypes();
        Object[] priceRange = roomRepository.findPriceRange();

        RoomsResponseDto response = new RoomsResponseDto();
        response.setRoomTypes(roomTypes);
        if (priceRange != null && priceRange.length > 0 && priceRange[0] != null) {
            Object[] range = (Object[]) priceRange[0];
            if (range.length >= 2) {
                response.setMinPrice((Long) range[0]);
                response.setMaxPrice((Long) range[1]);
            }
        }
        return ResponseEntity.ok(response);
    }

}
