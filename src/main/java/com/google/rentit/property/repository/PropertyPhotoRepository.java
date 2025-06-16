package com.google.rentit.property.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.google.rentit.property.model.PropertyPhoto;

public interface PropertyPhotoRepository extends JpaRepository<PropertyPhoto, Long> {

    List<PropertyPhoto> findByPropertyIdOrderByUploadedAtAsc(Long propertyId);


    
}
