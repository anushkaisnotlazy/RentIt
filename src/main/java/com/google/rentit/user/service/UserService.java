package com.google.rentit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.google.rentit.exception.ResourceNotFoundException;
import com.google.rentit.user.model.User;
import com.google.rentit.user.repository.UserRepository;

@Service
@CrossOrigin
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getUserByGoogleEmail(String googleEmail) {
        return userRepository.findByGoogleEmail(googleEmail)
            .orElseThrow(()-> new ResourceNotFoundException("User not found with email: " + googleEmail));
    }

    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);

        existingUser.setGoogleEmail(user.getGoogleEmail());
        existingUser.setUserName(user.getUserName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setBio(user.getBio());
        existingUser.setLivingHabits(user.getLivingHabits());
        existingUser.setInterests(user.getInterests());
        existingUser.setGender(user.getGender());

        return userRepository.save(existingUser);
    }

    public User createUser(User user) {
        if (userRepository.findByGoogleEmail(user.getGoogleEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + user.getGoogleEmail() + " already exists.");
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}