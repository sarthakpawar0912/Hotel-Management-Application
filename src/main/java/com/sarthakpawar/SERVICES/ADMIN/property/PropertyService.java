package com.sarthakpawar.SERVICES.ADMIN.property;

import com.sarthakpawar.DTO.PropertyDto;

import java.util.List;

public interface PropertyService {

    PropertyDto createProperty(PropertyDto propertyDto);

    PropertyDto updateProperty(Long id, PropertyDto propertyDto);

    PropertyDto getPropertyById(Long id);

    PropertyDto getPropertyByName(String name);

    List<PropertyDto> getAllProperties();

    List<PropertyDto> getActiveProperties();

    List<PropertyDto> getPropertiesByCity(String city);

    List<PropertyDto> getPropertiesByState(String state);

    List<PropertyDto> getPropertiesByCountry(String country);

    List<PropertyDto> getPropertiesByMinRating(Integer rating);

    PropertyDto activateProperty(Long id);

    PropertyDto deactivateProperty(Long id);

    void deleteProperty(Long id);

    PropertyDto updatePropertyLogo(Long id, byte[] logo);

    PropertyDto updatePropertyPolicies(Long id, String cancellationPolicy, String termsAndConditions);
}
