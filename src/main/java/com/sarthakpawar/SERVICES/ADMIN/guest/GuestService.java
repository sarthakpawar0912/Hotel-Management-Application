package com.sarthakpawar.SERVICES.ADMIN.guest;

import com.sarthakpawar.DTO.GuestDto;
import com.sarthakpawar.ENUMS.LoyaltyTier;

import java.util.List;

public interface GuestService {

    GuestDto createGuest(GuestDto guestDto);

    GuestDto updateGuest(Long id, GuestDto guestDto);

    GuestDto getGuestById(Long id);

    GuestDto getGuestByUserId(Long userId);

    GuestDto getGuestByEmail(String email);

    GuestDto getGuestByPhone(String phone);

    List<GuestDto> getAllGuests();

    List<GuestDto> getVipGuests();

    List<GuestDto> getBlacklistedGuests();

    List<GuestDto> getGuestsByLoyaltyTier(LoyaltyTier tier);

    List<GuestDto> searchGuestsByName(String name);

    List<GuestDto> getTopSpenders();

    List<GuestDto> getFrequentGuests();

    void addLoyaltyPoints(Long guestId, Integer points);

    void deductLoyaltyPoints(Long guestId, Integer points);

    void updateLoyaltyTier(Long guestId);

    void markAsVip(Long guestId, boolean isVip);

    void blacklistGuest(Long guestId, String reason);

    void removeFromBlacklist(Long guestId);

    void updateGuestStayStats(Long guestId, Long amountSpent);

    void deleteGuest(Long id);

    boolean existsByIdProofNumber(String idProofNumber);
}
