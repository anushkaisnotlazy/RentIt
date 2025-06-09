package com.google.rentit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.rentit.auth.service.AuthService;
import com.google.rentit.exception.ResourceNotFoundException;
import com.google.rentit.user.model.User;
import com.google.rentit.user.repository.UserRepository;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;


    // public UserService(UserRepository userRepository) {
    //     this.userRepository = userRepository;
    // }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User updateUser(Long id, User user) {
        return userRepository.save(user);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
        return user;
    }

    // public User signUpUser(String googleEmail, String userName, Role role, String bio, String livingHabits, String interests,
    //                        String googleId, Double preferredRadiusKm, String preferredLocationPincode, String phoneNumber, String password) {

    //     if (userRepository.findByGoogleEmail(googleEmail).isPresent()) {
    //         throw new IllegalArgumentException("User with this email already exists: " + googleEmail);
    //     }

    //     String hashedPassword = passwordEncoder.encode(password);

    //     User newUser = authService.signUp(googleEmail, userName, role, bio, livingHabits, interests,
    //             googleId, preferredRadiusKm, preferredLocationPincode, phoneNumber, hashedPassword); // Pass hashed password

    //     return userRepository.save(newUser);
    // }

}