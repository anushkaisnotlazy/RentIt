package com.google.rentit.property.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.google.rentit.property.model.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    // This class will handle
    
}
