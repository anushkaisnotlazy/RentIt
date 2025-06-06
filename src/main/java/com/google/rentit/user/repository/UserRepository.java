package com.google.rentit.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.google.rentit.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByGoogleEmail(String googleEmail);
    Optional<User> findByUserName(String userName);

}
