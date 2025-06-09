package com.google.rentit.property.dto;

import java.sql.Date;

import com.google.rentit.common.enums.ListingType;
import com.google.rentit.common.enums.PropertyType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PropertyUpdateDTO {

    @NotNull(message = "Property type cannot be null")
    private PropertyType propertyType;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 4000, message = "Description must be at most 4000 characters")
    private String description;

    @NotBlank(message = "Address cannot be blank")
    @Size(max = 500, message = "Address must be at most 500 characters")
    private String address;

    @NotNull(message = "Rent monthly cannot be null")
    private Double rentMonthly;

    @NotNull(message = "Number of bedrooms cannot be null")
    @Min(value = 0, message = "Number of bedrooms cannot be negative")
    private Integer numberOfBedrooms;

    @NotNull(message = "Number of bathrooms cannot be null")
    @Min(value = 0, message = "Number of bathrooms cannot be negative")
    private Integer numberOfBathrooms;

    @Pattern(regexp = "^\\{.*\\}$|^\\[.*\\]$", message = "Amenities must be a valid JSON object or array string")
    private String amenities;

    @NotNull(message = "Availability start date cannot be null")
    private Date availabilityStartDate;

    @NotNull(message = "Is available for flatmates status cannot be null")
    private Boolean isAvailableForFlatmates;

    @NotNull(message = "Is available for rent status cannot be null")
    private Boolean isAvailableForRent;

    @NotBlank(message = "Country cannot be blank")
    @Size(max = 100, message = "Country must be at most 100 characters")
    private String country;

    @NotBlank(message = "State cannot be blank")
    @Size(max = 100, message = "State must be at most 100 characters")
    private String state;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    @NotNull(message = "Listing type cannot be null")
    private ListingType listingType;
} 