package com.sarthakpawar.CONTROLLER.Customer;

import com.sarthakpawar.DTO.RoomsResponseDto;
import com.sarthakpawar.REPOSITORY.RoomRepository;
import com.sarthakpawar.SERVICES.CUSTOMER.room.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin("*")
public class CustomerRoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/rooms/{pageNumber}")
    public ResponseEntity<?> getAvailableRooms(@PathVariable int pageNumber){
        return ResponseEntity.ok(roomService.getAvailableRooms(pageNumber));
    }

    // Search and filter available rooms
    @GetMapping("/rooms/search")
    public ResponseEntity<?> searchAvailableRooms(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(defaultValue = "0") int pageNumber) {

        RoomsResponseDto response = roomService.searchAvailableRooms(
                name, type, minPrice, maxPrice, capacity, pageNumber
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
