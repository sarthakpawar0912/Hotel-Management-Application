package com.sarthakpawar.CONTROLLER.ADMIN;

import com.sarthakpawar.DTO.GuestDto;
import com.sarthakpawar.ENUMS.LoyaltyTier;
import com.sarthakpawar.SERVICES.ADMIN.guest.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/guests")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GuestController {

    private final GuestService guestService;

    @PostMapping
    public ResponseEntity<GuestDto> createGuest(@RequestBody GuestDto guestDto) {
        GuestDto createdGuest = guestService.createGuest(guestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGuest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuestDto> updateGuest(@PathVariable Long id, @RequestBody GuestDto guestDto) {
        GuestDto updatedGuest = guestService.updateGuest(id, guestDto);
        return ResponseEntity.ok(updatedGuest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestDto> getGuestById(@PathVariable Long id) {
        GuestDto guest = guestService.getGuestById(id);
        return ResponseEntity.ok(guest);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GuestDto> getGuestByUserId(@PathVariable Long userId) {
        GuestDto guest = guestService.getGuestByUserId(userId);
        return ResponseEntity.ok(guest);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<GuestDto> getGuestByEmail(@PathVariable String email) {
        GuestDto guest = guestService.getGuestByEmail(email);
        return ResponseEntity.ok(guest);
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<GuestDto> getGuestByPhone(@PathVariable String phone) {
        GuestDto guest = guestService.getGuestByPhone(phone);
        return ResponseEntity.ok(guest);
    }

    @GetMapping
    public ResponseEntity<List<GuestDto>> getAllGuests() {
        List<GuestDto> guests = guestService.getAllGuests();
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/vip")
    public ResponseEntity<List<GuestDto>> getVipGuests() {
        List<GuestDto> guests = guestService.getVipGuests();
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/blacklisted")
    public ResponseEntity<List<GuestDto>> getBlacklistedGuests() {
        List<GuestDto> guests = guestService.getBlacklistedGuests();
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/loyalty/{tier}")
    public ResponseEntity<List<GuestDto>> getGuestsByLoyaltyTier(@PathVariable LoyaltyTier tier) {
        List<GuestDto> guests = guestService.getGuestsByLoyaltyTier(tier);
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GuestDto>> searchGuests(@RequestParam String name) {
        List<GuestDto> guests = guestService.searchGuestsByName(name);
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/top-spenders")
    public ResponseEntity<List<GuestDto>> getTopSpenders() {
        List<GuestDto> guests = guestService.getTopSpenders();
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/frequent")
    public ResponseEntity<List<GuestDto>> getFrequentGuests() {
        List<GuestDto> guests = guestService.getFrequentGuests();
        return ResponseEntity.ok(guests);
    }

    @PostMapping("/{id}/loyalty/add")
    public ResponseEntity<Void> addLoyaltyPoints(@PathVariable Long id, @RequestParam Integer points) {
        guestService.addLoyaltyPoints(id, points);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/loyalty/deduct")
    public ResponseEntity<Void> deductLoyaltyPoints(@PathVariable Long id, @RequestParam Integer points) {
        guestService.deductLoyaltyPoints(id, points);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/vip")
    public ResponseEntity<Void> markAsVip(@PathVariable Long id, @RequestParam boolean isVip) {
        guestService.markAsVip(id, isVip);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/blacklist")
    public ResponseEntity<Void> blacklistGuest(@PathVariable Long id, @RequestParam String reason) {
        guestService.blacklistGuest(id, reason);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/blacklist")
    public ResponseEntity<Void> removeFromBlacklist(@PathVariable Long id) {
        guestService.removeFromBlacklist(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }
}
