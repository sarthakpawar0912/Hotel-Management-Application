package com.sarthakpawar.CONTROLLER.ADMIN;

import com.sarthakpawar.DTO.PropertyDto;
import com.sarthakpawar.SERVICES.ADMIN.property.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/properties")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    public ResponseEntity<PropertyDto> createProperty(@RequestBody PropertyDto propertyDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.createProperty(propertyDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDto> updateProperty(@PathVariable Long id, @RequestBody PropertyDto propertyDto) {
        return ResponseEntity.ok(propertyService.updateProperty(id, propertyDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDto> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PropertyDto> getPropertyByName(@PathVariable String name) {
        return ResponseEntity.ok(propertyService.getPropertyByName(name));
    }

    @GetMapping
    public ResponseEntity<List<PropertyDto>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/active")
    public ResponseEntity<List<PropertyDto>> getActiveProperties() {
        return ResponseEntity.ok(propertyService.getActiveProperties());
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<PropertyDto>> getPropertiesByCity(@PathVariable String city) {
        return ResponseEntity.ok(propertyService.getPropertiesByCity(city));
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<PropertyDto>> getPropertiesByState(@PathVariable String state) {
        return ResponseEntity.ok(propertyService.getPropertiesByState(state));
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<PropertyDto>> getPropertiesByCountry(@PathVariable String country) {
        return ResponseEntity.ok(propertyService.getPropertiesByCountry(country));
    }

    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<PropertyDto>> getPropertiesByMinRating(@PathVariable Integer rating) {
        return ResponseEntity.ok(propertyService.getPropertiesByMinRating(rating));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<PropertyDto> activateProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.activateProperty(id));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<PropertyDto> deactivateProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.deactivateProperty(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PropertyDto> updatePropertyLogo(
            @PathVariable Long id,
            @RequestParam("logo") MultipartFile logo) throws IOException {
        return ResponseEntity.ok(propertyService.updatePropertyLogo(id, logo.getBytes()));
    }

    @PutMapping("/{id}/policies")
    public ResponseEntity<PropertyDto> updatePropertyPolicies(
            @PathVariable Long id,
            @RequestParam(required = false) String cancellationPolicy,
            @RequestParam(required = false) String termsAndConditions) {
        return ResponseEntity.ok(propertyService.updatePropertyPolicies(id, cancellationPolicy, termsAndConditions));
    }
}
