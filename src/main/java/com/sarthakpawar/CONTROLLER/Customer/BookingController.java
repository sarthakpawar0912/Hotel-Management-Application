package com.sarthakpawar.CONTROLLER.Customer;

import com.sarthakpawar.DTO.ReservationDto;
import com.sarthakpawar.SERVICES.CUSTOMER.bookings.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin("*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/book")
    public ResponseEntity<?> postBooking(@RequestBody ReservationDto reservationDto){
        boolean success=bookingService.postReservation(reservationDto);

        if (success){
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/bookings/{userId}/{pageNumber}")
    public ResponseEntity<?> getAllBookingsByUserId(@PathVariable Long userId, @PathVariable int pageNumber){
        try{
            return ResponseEntity.ok(bookingService.getAllReservationByUserId(userId,pageNumber));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

}
