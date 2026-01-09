package com.sarthakpawar.SERVICES.ADMIN.availability;

import com.sarthakpawar.DTO.RoomAvailabilityDto;
import com.sarthakpawar.ENTITY.Reservation;
import com.sarthakpawar.ENTITY.Room;
import com.sarthakpawar.ENTITY.RoomAvailability;
import com.sarthakpawar.REPOSITORY.ReservationRepository;
import com.sarthakpawar.REPOSITORY.RoomAvailabilityRepository;
import com.sarthakpawar.REPOSITORY.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomAvailabilityServiceImpl implements RoomAvailabilityService {

    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public RoomAvailabilityDto createOrUpdateAvailability(Long roomId, LocalDate date, RoomAvailabilityDto dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));

        RoomAvailability availability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, date)
                .orElse(new RoomAvailability());

        availability.setRoom(room);
        availability.setDate(date);

        if (dto.getIsAvailable() != null) {
            availability.setIsAvailable(dto.getIsAvailable());
        }
        if (dto.getIsBlocked() != null) {
            availability.setIsBlocked(dto.getIsBlocked());
        }
        if (dto.getBlockReason() != null) {
            availability.setBlockReason(dto.getBlockReason());
        }
        if (dto.getCustomPrice() != null) {
            availability.setCustomPrice(dto.getCustomPrice());
        }
        if (dto.getMinStay() != null) {
            availability.setMinStay(dto.getMinStay());
        }

        RoomAvailability saved = roomAvailabilityRepository.save(availability);
        return saved.getRoomAvailabilityDto();
    }

    @Override
    @Transactional
    public void createAvailabilityForDateRange(Long roomId, LocalDate startDate, LocalDate endDate) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if (roomAvailabilityRepository.findByRoomIdAndDate(roomId, current).isEmpty()) {
                RoomAvailability availability = new RoomAvailability();
                availability.setRoom(room);
                availability.setDate(current);
                availability.setIsAvailable(true);
                availability.setIsBlocked(false);
                roomAvailabilityRepository.save(availability);
            }
            current = current.plusDays(1);
        }
    }

    @Override
    public RoomAvailabilityDto getAvailability(Long roomId, LocalDate date) {
        RoomAvailability availability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, date)
                .orElse(null);

        if (availability == null) {
            // Return default availability
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new EntityNotFoundException("Room not found"));

            RoomAvailabilityDto dto = new RoomAvailabilityDto();
            dto.setRoomId(roomId);
            dto.setRoomName(room.getName());
            dto.setDate(date);
            dto.setIsAvailable(true);
            dto.setIsBlocked(false);
            dto.setCustomPrice(room.getPrice());
            return dto;
        }

        return availability.getRoomAvailabilityDto();
    }

    @Override
    public List<RoomAvailabilityDto> getAvailabilityByRoom(Long roomId) {
        return roomAvailabilityRepository.findByRoomId(roomId).stream()
                .map(RoomAvailability::getRoomAvailabilityDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomAvailabilityDto> getAvailabilityByRoomAndDateRange(Long roomId, LocalDate startDate, LocalDate endDate) {
        return roomAvailabilityRepository.findByRoomIdAndDateRange(roomId, startDate, endDate).stream()
                .map(RoomAvailability::getRoomAvailabilityDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomAvailabilityDto> getAvailabilityByDateRange(LocalDate startDate, LocalDate endDate) {
        return roomAvailabilityRepository.findByDateRange(startDate, endDate).stream()
                .map(RoomAvailability::getRoomAvailabilityDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void blockRoom(Long roomId, LocalDate startDate, LocalDate endDate, String reason) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            RoomAvailability availability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, current)
                    .orElse(new RoomAvailability());

            availability.setRoom(room);
            availability.setDate(current);
            availability.setIsBlocked(true);
            availability.setBlockReason(reason);
            availability.setIsAvailable(false);

            roomAvailabilityRepository.save(availability);
            current = current.plusDays(1);
        }
    }

    @Override
    @Transactional
    public void unblockRoom(Long roomId, LocalDate startDate, LocalDate endDate) {
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            roomAvailabilityRepository.findByRoomIdAndDate(roomId, current)
                    .ifPresent(availability -> {
                        availability.setIsBlocked(false);
                        availability.setBlockReason(null);
                        availability.setIsAvailable(true);
                        roomAvailabilityRepository.save(availability);
                    });
            current = current.plusDays(1);
        }
    }

    @Override
    public List<RoomAvailabilityDto> getBlockedDates() {
        return roomAvailabilityRepository.findAllBlockedDates().stream()
                .map(RoomAvailability::getRoomAvailabilityDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void setCustomPrice(Long roomId, LocalDate date, Long price) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));

        RoomAvailability availability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, date)
                .orElse(new RoomAvailability());

        availability.setRoom(room);
        availability.setDate(date);
        availability.setCustomPrice(price);

        if (availability.getIsAvailable() == null) {
            availability.setIsAvailable(true);
        }
        if (availability.getIsBlocked() == null) {
            availability.setIsBlocked(false);
        }

        roomAvailabilityRepository.save(availability);
    }

    @Override
    @Transactional
    public void setCustomPriceForRange(Long roomId, LocalDate startDate, LocalDate endDate, Long price) {
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            setCustomPrice(roomId, current, price);
            current = current.plusDays(1);
        }
    }

    @Override
    @Transactional
    public void removeCustomPrice(Long roomId, LocalDate date) {
        roomAvailabilityRepository.findByRoomIdAndDate(roomId, date)
                .ifPresent(availability -> {
                    availability.setCustomPrice(null);
                    roomAvailabilityRepository.save(availability);
                });
    }

    @Override
    @Transactional
    public void setMinimumStay(Long roomId, LocalDate startDate, LocalDate endDate, Integer minStay) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            RoomAvailability availability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, current)
                    .orElse(new RoomAvailability());

            availability.setRoom(room);
            availability.setDate(current);
            availability.setMinStay(minStay);

            if (availability.getIsAvailable() == null) {
                availability.setIsAvailable(true);
            }
            if (availability.getIsBlocked() == null) {
                availability.setIsBlocked(false);
            }

            roomAvailabilityRepository.save(availability);
            current = current.plusDays(1);
        }
    }

    @Override
    public boolean isRoomAvailable(Long roomId, LocalDate startDate, LocalDate endDate) {
        // Check room availability records
        List<RoomAvailability> availabilityRecords =
                roomAvailabilityRepository.findByRoomIdAndDateRange(roomId, startDate, endDate.minusDays(1));

        for (RoomAvailability record : availabilityRecords) {
            if (!record.getIsAvailable() || record.getIsBlocked()) {
                return false;
            }
        }

        // Check existing reservations
        List<Reservation> overlappingReservations = reservationRepository.findAll().stream()
                .filter(r -> r.getRoom().getId().equals(roomId))
                .filter(r -> r.getReservationStatus() != com.sarthakpawar.ENUMS.ReservationStatus.CANCELLED)
                .filter(r -> {
                    // Check if date ranges overlap
                    return !r.getCheckOutDate().isBefore(startDate) && !r.getCheckInDate().isAfter(endDate.minusDays(1));
                })
                .toList();

        return overlappingReservations.isEmpty();
    }

    @Override
    public List<Long> getAvailableRoomIds(LocalDate startDate, LocalDate endDate) {
        List<Room> allRooms = roomRepository.findAll();

        return allRooms.stream()
                .filter(room -> isRoomAvailable(room.getId(), startDate, endDate))
                .map(Room::getId)
                .collect(Collectors.toList());
    }

    @Override
    public Map<LocalDate, List<RoomAvailabilityDto>> getCalendarView(LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, List<RoomAvailabilityDto>> calendar = new LinkedHashMap<>();

        List<Room> rooms = roomRepository.findAll();
        List<RoomAvailability> availabilityRecords =
                roomAvailabilityRepository.findByDateRange(startDate, endDate);

        Map<String, RoomAvailability> availabilityMap = availabilityRecords.stream()
                .collect(Collectors.toMap(
                        a -> a.getRoom().getId() + "_" + a.getDate(),
                        a -> a
                ));

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            List<RoomAvailabilityDto> dayAvailability = new ArrayList<>();

            for (Room room : rooms) {
                String key = room.getId() + "_" + current;
                RoomAvailability availability = availabilityMap.get(key);

                RoomAvailabilityDto dto;
                if (availability != null) {
                    dto = availability.getRoomAvailabilityDto();
                } else {
                    dto = new RoomAvailabilityDto();
                    dto.setRoomId(room.getId());
                    dto.setRoomName(room.getName());
                    dto.setDate(current);
                    dto.setIsAvailable(true);
                    dto.setIsBlocked(false);
                    dto.setCustomPrice(room.getPrice());
                }

                dayAvailability.add(dto);
            }

            calendar.put(current, dayAvailability);
            current = current.plusDays(1);
        }

        return calendar;
    }

    @Override
    public Map<String, Object> getRoomCalendar(Long roomId, int year, int month) {
        Map<String, Object> calendar = new HashMap<>();

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<RoomAvailabilityDto> availability =
                getAvailabilityByRoomAndDateRange(roomId, startDate, endDate);

        // Fill in missing dates with default values
        Map<LocalDate, RoomAvailabilityDto> availabilityMap = availability.stream()
                .collect(Collectors.toMap(RoomAvailabilityDto::getDate, a -> a));

        List<Map<String, Object>> days = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            Map<String, Object> day = new HashMap<>();
            day.put("date", current);
            day.put("dayOfWeek", current.getDayOfWeek().toString());

            RoomAvailabilityDto dayAvailability = availabilityMap.get(current);
            if (dayAvailability != null) {
                day.put("isAvailable", dayAvailability.getIsAvailable());
                day.put("isBlocked", dayAvailability.getIsBlocked());
                day.put("blockReason", dayAvailability.getBlockReason());
                day.put("price", dayAvailability.getCustomPrice());
                day.put("minStay", dayAvailability.getMinStay());
                day.put("reservationId", dayAvailability.getReservationId());
            } else {
                day.put("isAvailable", true);
                day.put("isBlocked", false);
                day.put("price", room.getPrice());
            }

            days.add(day);
            current = current.plusDays(1);
        }

        calendar.put("roomId", roomId);
        calendar.put("roomName", room.getName());
        calendar.put("year", year);
        calendar.put("month", month);
        calendar.put("days", days);

        return calendar;
    }

    @Override
    @Transactional
    public void markAsBooked(Long roomId, LocalDate startDate, LocalDate endDate, Long reservationId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));

        Reservation reservation = null;
        if (reservationId != null) {
            reservation = reservationRepository.findById(reservationId).orElse(null);
        }

        LocalDate current = startDate;
        while (current.isBefore(endDate)) {
            RoomAvailability availability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, current)
                    .orElse(new RoomAvailability());

            availability.setRoom(room);
            availability.setDate(current);
            availability.setIsAvailable(false);
            availability.setReservation(reservation);

            roomAvailabilityRepository.save(availability);
            current = current.plusDays(1);
        }
    }

    @Override
    @Transactional
    public void markAsAvailable(Long roomId, LocalDate startDate, LocalDate endDate) {
        LocalDate current = startDate;
        while (current.isBefore(endDate)) {
            roomAvailabilityRepository.findByRoomIdAndDate(roomId, current)
                    .ifPresent(availability -> {
                        availability.setIsAvailable(true);
                        availability.setReservation(null);
                        roomAvailabilityRepository.save(availability);
                    });
            current = current.plusDays(1);
        }
    }
}
