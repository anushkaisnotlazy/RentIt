package com.google.rentit.property.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.google.rentit.property.model.Property;

@Repository
@CrossOrigin
public interface PropertyRepository extends JpaRepository<Property, Long> {

    public Page<Property> findAll(Specification<Property> spec, Pageable pageable);

    
}
