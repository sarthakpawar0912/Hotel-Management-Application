package com.sarthakpawar.SERVICES.CUSTOMER.bookings;

import com.sarthakpawar.DTO.ReservationDto;
import com.sarthakpawar.DTO.ReservationResponseDto;

public interface BookingService {

    boolean postReservation(ReservationDto reservationDto);

    ReservationResponseDto getAllReservationByUserId(Long userId, int pageNumber);

}
