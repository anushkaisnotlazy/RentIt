package com.google.rentit.property.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.rentit.property.dto.PropertyCreationDTO;
import com.google.rentit.property.model.Property;
import com.google.rentit.property.repository.PropertyRepository;
import com.google.rentit.property.service.PropertyService;

@RestController
@RequestMapping("/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PropertyRepository propertyRepository;

    @GetMapping("/filter")
    public ResponseEntity<List<Property>> filterProperties(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<Property> properties = propertyService.filterProperties(country, state, city, propertyType, minPrice, maxPrice);
        return ResponseEntity.ok(properties);
    }

    @PostMapping("/add")
    public ResponseEntity<Property> addProperty(@RequestBody PropertyCreationDTO propertyCreationDTO) {
        Property property = propertyService.addProperty(propertyCreationDTO);
        return ResponseEntity.ok(propertyRepository.findById(property.getId()).orElse(null));
    }

    @PutMapping("/add/{id}")
    public ResponseEntity<Property> editProperty(@PathVariable Long id, @RequestBody PropertyCreationDTO propertyCreationDTO) {
        Property updatedProperty = propertyService.editProperty(id, propertyCreationDTO);
        return ResponseEntity.ok(updatedProperty);
    }

    @GetMapping("/all")
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }
    
}