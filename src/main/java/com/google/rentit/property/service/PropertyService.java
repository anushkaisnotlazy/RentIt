package com.google.rentit.property.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.rentit.property.dto.PropertyCreationDTO;
import com.google.rentit.property.model.Property;
import com.google.rentit.property.repository.PropertyRepository;
import com.google.rentit.user.model.User;
import com.google.rentit.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Property> filterProperties(String country, String state, String city, String propertyType, Double minPrice, Double maxPrice) {
        return propertyRepository.findAll();
    }

    // public Property addProperty(PropertyCreationDTO propertyCreationDTO) {
    //     Property property = new Property();
    //     mapDtoToEntity(propertyCreationDTO, property);
    //     return propertyRepository.save(property);
    // }

    public Property addProperty(PropertyCreationDTO propertyCreationDTO) {
        Optional<User> optionalUser = userRepository.findById(propertyCreationDTO.getRenterId());
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found");
        }
        User renter = optionalUser.get();
        Property property = new Property();
        mapDtoToEntity(propertyCreationDTO, property);
        property.setRenter(renter);
        return propertyRepository.save(property);
    }

    public Property editProperty(Long id, PropertyCreationDTO propertyCreationDTO) {
        Optional<Property> optionalProperty = propertyRepository.findById(id);
        if (optionalProperty.isEmpty()) {
            throw new RuntimeException("Property not found");
        }
        Property property = optionalProperty.get();
        mapDtoToEntity(propertyCreationDTO, property);
        return propertyRepository.save(property);
    }

    private void mapDtoToEntity(PropertyCreationDTO dto, Property property) {
        property.setPropertyType(dto.getPropertyType());
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setAddress(dto.getAddress());
        property.setRentMonthly(dto.getRentMonthly());
        property.setNumberOfBedrooms(dto.getNumberOfBedrooms());
        property.setNumberOfBathrooms(dto.getNumberOfBathrooms());
        property.setAmenities(dto.getAmenities());
        property.setAvailabilityStartDate(dto.getAvailabilityStartDate());
        property.setIsAvailableForFlatmates(dto.getIsAvailableForFlatmates());
        property.setIsAvailableForRent(dto.getIsAvailableForRent());
        property.setCountry(dto.getCountry());
        property.setState(dto.getState());
        property.setCity(dto.getCity());
        property.setListingType(dto.getListingType());
        property.setLookingFor(dto.getLookingFor());
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

}
