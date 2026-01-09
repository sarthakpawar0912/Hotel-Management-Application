package com.sarthakpawar.SERVICES.ADMIN.property;

import com.sarthakpawar.DTO.PropertyDto;
import com.sarthakpawar.ENTITY.Property;
import com.sarthakpawar.REPOSITORY.PropertyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;

    @Override
    @Transactional
    public PropertyDto createProperty(PropertyDto propertyDto) {
        Property property = new Property();
        mapDtoToEntity(propertyDto, property);

        Property savedProperty = propertyRepository.save(property);
        return savedProperty.getPropertyDto();
    }

    @Override
    @Transactional
    public PropertyDto updateProperty(Long id, PropertyDto propertyDto) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        mapDtoToEntity(propertyDto, property);

        Property updatedProperty = propertyRepository.save(property);
        return updatedProperty.getPropertyDto();
    }

    @Override
    public PropertyDto getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        return property.getPropertyDto();
    }

    @Override
    public PropertyDto getPropertyByName(String name) {
        Property property = propertyRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        return property.getPropertyDto();
    }

    @Override
    public List<PropertyDto> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(Property::getPropertyDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyDto> getActiveProperties() {
        return propertyRepository.findByIsActiveTrue().stream()
                .map(Property::getPropertyDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyDto> getPropertiesByCity(String city) {
        return propertyRepository.findByCity(city).stream()
                .map(Property::getPropertyDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyDto> getPropertiesByState(String state) {
        return propertyRepository.findByState(state).stream()
                .map(Property::getPropertyDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyDto> getPropertiesByCountry(String country) {
        return propertyRepository.findByCountry(country).stream()
                .map(Property::getPropertyDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyDto> getPropertiesByMinRating(Integer rating) {
        return propertyRepository.findByStarRatingGreaterThanEqual(rating).stream()
                .map(Property::getPropertyDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PropertyDto activateProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        property.setIsActive(true);
        Property updatedProperty = propertyRepository.save(property);
        return updatedProperty.getPropertyDto();
    }

    @Override
    @Transactional
    public PropertyDto deactivateProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        property.setIsActive(false);
        Property updatedProperty = propertyRepository.save(property);
        return updatedProperty.getPropertyDto();
    }

    @Override
    @Transactional
    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new EntityNotFoundException("Property not found");
        }
        propertyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PropertyDto updatePropertyLogo(Long id, byte[] logo) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        property.setLogo(logo);
        Property updatedProperty = propertyRepository.save(property);
        return updatedProperty.getPropertyDto();
    }

    @Override
    @Transactional
    public PropertyDto updatePropertyPolicies(Long id, String cancellationPolicy, String termsAndConditions) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        property.setCancellationPolicy(cancellationPolicy);
        property.setTermsAndConditions(termsAndConditions);

        Property updatedProperty = propertyRepository.save(property);
        return updatedProperty.getPropertyDto();
    }

    private void mapDtoToEntity(PropertyDto dto, Property entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setAddress(dto.getAddress());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setCountry(dto.getCountry());
        entity.setZipCode(dto.getZipCode());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setWebsite(dto.getWebsite());
        entity.setLogo(dto.getLogo());
        entity.setCheckInTime(dto.getCheckInTime());
        entity.setCheckOutTime(dto.getCheckOutTime());
        entity.setCancellationPolicy(dto.getCancellationPolicy());
        entity.setTermsAndConditions(dto.getTermsAndConditions());
        entity.setAmenities(dto.getAmenities());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setStarRating(dto.getStarRating());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
    }
}
