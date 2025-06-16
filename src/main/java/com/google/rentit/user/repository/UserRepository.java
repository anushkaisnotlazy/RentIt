package com.google.rentit.user.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.google.rentit.user.model.User;

@Repository
@CrossOrigin
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByGoogleEmail(String googleEmail);
    Optional<User> findByUserName(String userName);
    // Optional<User> updateByGoogleEmail(String googleEmail);
}

