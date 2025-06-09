package com.google.rentit.property.dto;

import com.google.rentit.common.enums.PropertyType;

import lombok.Data;

@Data
public class PropertyFilterDTO {

    private String country;
    private String state;
    private String city;
    private PropertyType propertyType;
    private Double minPrice;
    private Double maxPrice;
}

