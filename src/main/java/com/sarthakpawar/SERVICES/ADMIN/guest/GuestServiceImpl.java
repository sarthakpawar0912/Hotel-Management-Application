package com.sarthakpawar.SERVICES.ADMIN.guest;

import com.sarthakpawar.DTO.GuestDto;
import com.sarthakpawar.ENTITY.Guest;
import com.sarthakpawar.ENTITY.User;
import com.sarthakpawar.ENUMS.LoyaltyTier;
import com.sarthakpawar.REPOSITORY.GuestRepository;
import com.sarthakpawar.REPOSITORY.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public GuestDto createGuest(GuestDto guestDto) {
        Guest guest = new Guest();
        mapDtoToEntity(guestDto, guest);

        if (guestDto.getUserId() != null) {
            User user = userRepository.findById(guestDto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            guest.setUser(user);
        }

        guest.setLoyaltyPoints(0);
        guest.setLoyaltyTier(LoyaltyTier.BRONZE);
        guest.setTotalStays(0);
        guest.setTotalSpent(0L);

        Guest savedGuest = guestRepository.save(guest);
        return savedGuest.getGuestDto();
    }

    @Override
    @Transactional
    public GuestDto updateGuest(Long id, GuestDto guestDto) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found with id: " + id));

        mapDtoToEntity(guestDto, guest);
        Guest updatedGuest = guestRepository.save(guest);
        return updatedGuest.getGuestDto();
    }

    @Override
    public GuestDto getGuestById(Long id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found with id: " + id));
        return guest.getGuestDto();
    }

    @Override
    public GuestDto getGuestByUserId(Long userId) {
        Guest guest = guestRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found for user id: " + userId));
        return guest.getGuestDto();
    }

    @Override
    public GuestDto getGuestByEmail(String email) {
        Guest guest = guestRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found with email: " + email));
        return guest.getGuestDto();
    }

    @Override
    public GuestDto getGuestByPhone(String phone) {
        Guest guest = guestRepository.findByPhone(phone)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found with phone: " + phone));
        return guest.getGuestDto();
    }

    @Override
    public List<GuestDto> getAllGuests() {
        return guestRepository.findAll().stream()
                .map(Guest::getGuestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GuestDto> getVipGuests() {
        return guestRepository.findByIsVipTrue().stream()
                .map(Guest::getGuestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GuestDto> getBlacklistedGuests() {
        return guestRepository.findByIsBlacklistedTrue().stream()
                .map(Guest::getGuestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GuestDto> getGuestsByLoyaltyTier(LoyaltyTier tier) {
        return guestRepository.findByLoyaltyTier(tier).stream()
                .map(Guest::getGuestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GuestDto> searchGuestsByName(String name) {
        return guestRepository.searchByName(name).stream()
                .map(Guest::getGuestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GuestDto> getTopSpenders() {
        return guestRepository.findTopSpenders().stream()
                .limit(20)
                .map(Guest::getGuestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GuestDto> getFrequentGuests() {
        return guestRepository.findFrequentGuests().stream()
                .limit(20)
                .map(Guest::getGuestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addLoyaltyPoints(Long guestId, Integer points) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found with id: " + guestId));

        guest.setLoyaltyPoints(guest.getLoyaltyPoints() + points);
        guestRepository.save(guest);
        updateLoyaltyTier(guestId);
    }

    @Override
    @Transactional
    public void deductLoyaltyPoints(Long guestId, Integer points) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found with id: " + guestId));

        int newPoints = Math.max(0, guest.getLoyaltyPoints() - points);
        guest.setLoyaltyPoints(newPoints);
        guestRepository.save(guest);
        updateLoyaltyTier(guestId);
    }

    @Override
    @Transactional
    public void updateLoyaltyTier(Long guestId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found with id: " + guestId));

        int points = guest.getLoyaltyPoints();
        LoyaltyTier newTier;

        if (points >= 10000) {
            newTier = LoyaltyTier.PLATINUM;
        } else if (points >= 5000) {
            newTier = LoyaltyTier.GOLD;
        } else if (points >= 2000) {
            newTier = LoyaltyTier.SILVER;
        } else {
            newTier = LoyaltyTier.BRONZE;
        }

        guest.setLoyaltyTier(newTier);
        guestRepository.save(guest);
    }

    @Override
    @Transactional
    public void markAsVip(Long guestId, boolean isVip) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found with id: " + guestId));

        guest.setIsVip(isVip);
        guestRepository.save(guest);
    }

    @Override
    @Transactional
    public void blacklistGuest(Long guestId, String reason) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found with id: " + guestId));

        guest.setIsBlacklisted(true);
        guest.setBlacklistReason(reason);
        guestRepository.save(guest);
    }

    @Override
    @Transactional
    public void removeFromBlacklist(Long guestId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found with id: " + guestId));

        guest.setIsBlacklisted(false);
        guest.setBlacklistReason(null);
        guestRepository.save(guest);
    }

    @Override
    @Transactional
    public void updateGuestStayStats(Long guestId, Long amountSpent) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found with id: " + guestId));

        guest.setTotalStays(guest.getTotalStays() + 1);
        guest.setTotalSpent(guest.getTotalSpent() + amountSpent);
        guest.setLastStayDate(LocalDateTime.now());

        // Award loyalty points (1 point per 100 spent)
        int pointsEarned = (int) (amountSpent / 100);
        guest.setLoyaltyPoints(guest.getLoyaltyPoints() + pointsEarned);

        guestRepository.save(guest);
        updateLoyaltyTier(guestId);
    }

    @Override
    @Transactional
    public void deleteGuest(Long id) {
        if (!guestRepository.existsById(id)) {
            throw new EntityNotFoundException("Guest not found with id: " + id);
        }
        guestRepository.deleteById(id);
    }

    @Override
    public boolean existsByIdProofNumber(String idProofNumber) {
        return guestRepository.findByIdProofNumber(idProofNumber).isPresent();
    }

    private void mapDtoToEntity(GuestDto dto, Guest entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setAlternatePhone(dto.getAlternatePhone());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setGender(dto.getGender());
        entity.setNationality(dto.getNationality());
        entity.setAddress(dto.getAddress());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setCountry(dto.getCountry());
        entity.setZipCode(dto.getZipCode());
        entity.setIdProofType(dto.getIdProofType());
        entity.setIdProofNumber(dto.getIdProofNumber());
        entity.setIdProofImage(dto.getIdProofImage());
        entity.setCompanyName(dto.getCompanyName());
        entity.setGstin(dto.getGstin());
        entity.setPreferences(dto.getPreferences());
        entity.setSpecialRequests(dto.getSpecialRequests());
    }
}
