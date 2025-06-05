package com.google.rentit.user.service;

import org.springframework.stereotype.Service;

import com.google.rentit.exception.ResourceNotFoundException;
import com.google.rentit.user.model.User;
import com.google.rentit.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

}