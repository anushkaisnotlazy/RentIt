package com.google.rentit.property.model;

import java.util.Date;

import org.springframework.web.bind.annotation.CrossOrigin;

import com.google.rentit.common.enums.ListingType;
import com.google.rentit.common.enums.LookingFor;
import com.google.rentit.common.enums.PropertyType;
import com.google.rentit.user.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "property")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@CrossOrigin
public class Property {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Owner user ID cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User renter; // Using User entity for FK relationship

    @NotNull(message = "Property type cannot be null")
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title must be at most 255 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 4000, message = "Description must be at most 4000 characters")
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotBlank(message = "Address cannot be blank")
    @Size(max = 500, message = "Address must be at most 500 characters")
    @Column(name = "address", nullable = false)
    private String address;

    // // PostGIS Point type for geospatial queries
    // @Column(name = "location_point", columnDefinition = "geometry(Point,4326)")
    // private Point locationPoint; // org.locationtech.jts.geom.Point

    @NotNull(message = "Rent monthly cannot be null")
    @Column(name = "rent_monthly", nullable = false)
    private Double rentMonthly;

    @NotNull(message = "Number of bedrooms cannot be null")
    @Column(name = "number_of_bedrooms", nullable = false)
    private Integer numberOfBedrooms;

    @NotNull(message = "Number of bathrooms cannot be null")
    @Column(name = "number_of_bathrooms", nullable = false)
    private Integer numberOfBathrooms;

    // JSONB type for flexible amenities (could also be a separate entity/join table)
    @Column(name = "amenities")
    private String amenities; // Example: ["Wi-Fi", "Parking", "Furnished"]

    @NotNull(message = "Availability start date cannot be null")
    @Column(name = "availability_start_date", nullable = false)
    private Date availabilityStartDate;

    @NotNull(message = "Is available for flatmates status cannot be null")
    @Column(name = "is_available_for_flatmates", nullable = false)
    private Boolean isAvailableForFlatmates;

    @NotNull(message = "Is available for rent status cannot be null")
    @Column(name = "is_available_for_rent", nullable = false)
    private Boolean isAvailableForRent;

    @NotBlank(message = "Country cannot be blank")
    @Size(max = 100, message = "Country must be at most 100 characters")
    @Column(name = "country", nullable = false)
    private String country;

    @NotBlank(message = "State cannot be blank")
    @Size(max = 100, message = "State must be at most 100 characters")
    @Column(name = "state", nullable = false)
    private String state;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 100, message = "City must be at most 100 characters")
    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "listing_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ListingType listingType; 

    @Column(name = "looking_for", nullable = false)
    @Enumerated(EnumType.STRING)
    private LookingFor lookingFor;

}

