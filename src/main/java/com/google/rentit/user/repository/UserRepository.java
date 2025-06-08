package com.google.rentit.user.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.google.rentit.user.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByGoogleEmail(String googleEmail);
    Optional<User> findByUserName(String userName);
    // Optional<User> updateByGoogleEmail(String googleEmail);
}

