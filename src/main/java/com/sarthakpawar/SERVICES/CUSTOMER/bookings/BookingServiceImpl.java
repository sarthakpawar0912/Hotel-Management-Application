package com.sarthakpawar.SERVICES.CUSTOMER.bookings;

import com.sarthakpawar.DTO.ReservationDto;
import com.sarthakpawar.DTO.ReservationResponseDto;
import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.ENTITY.Room;
import com.sarthakpawar.ENTITY.User;
import com.sarthakpawar.ENUMS.ReservationStatus;
import com.sarthakpawar.REPOSITORY.ReservationRepository;
import com.sarthakpawar.REPOSITORY.RoomRepository;
import com.sarthakpawar.REPOSITORY.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements  BookingService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public static final int SEARCH_RESULT_PER_PAGE=4;

    public boolean postReservation(ReservationDto reservationDto){
        Optional<User> optionalUser=userRepository.findById(reservationDto.getUserId());
        Optional<Room> optionalRoom=roomRepository.findById(reservationDto.getRoomId());
        if(optionalRoom.isPresent() && optionalRoom.isPresent()){
            Reservation reservation=new Reservation();
            reservation.setRoom(optionalRoom.get());
            reservation.setUser(optionalUser.get());
            reservation.setCheckInDate(reservationDto.getCheckInDate());
            reservation.setCheckOutDate(reservationDto.getCheckOutDate());
            reservation.setReservationStatus(ReservationStatus.PENDING);
            Long days= ChronoUnit.DAYS.between(reservationDto.getCheckInDate(), reservationDto.getCheckOutDate());
            reservation.setPrice(optionalRoom.get().getPrice()*days);
            reservationRepository.save(reservation);
            return  true;
        }
        return false;
    }

    public ReservationResponseDto getAllReservationByUserId(Long userId, int pageNumber){
        Pageable pageable= PageRequest.of(pageNumber, SEARCH_RESULT_PER_PAGE);
        Page<Reservation> reservationPage=reservationRepository.findAllByUserId(pageable,userId);
        ReservationResponseDto reservationResponseDto=new ReservationResponseDto();
        reservationResponseDto.setReservationDtoList(reservationPage.stream().map(Reservation::getReservationDto).collect(Collectors.toList()));
        reservationResponseDto.setPageNumber((reservationPage.getPageable().getPageNumber()));
        reservationResponseDto.setTotalPages(reservationPage.getTotalPages());
        return reservationResponseDto;
    }

}
