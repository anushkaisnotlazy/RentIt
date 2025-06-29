package com.google.rentit.auth.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.google.rentit.user.model.User;
import com.google.rentit.user.repository.UserRepository;

@Service
@CrossOrigin

public class AuthService {

    private final PasswordEncoder passwordEncoder;

    public AuthService() {
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    private UserRepository userRepository;


    public User signUp(String googleEmail, String userName, String bio, String livingHabits, String interests,
                        String phoneNumber, String rawPassword, String gender) {

        if (userRepository.findByGoogleEmail(googleEmail).isPresent()) {
            throw new IllegalArgumentException("User with email " + googleEmail + " already exists.");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);

        User user = new User();
        user.setGoogleEmail(googleEmail);
        user.setUserName(userName);
        user.setPhoneNumber(phoneNumber);
        user.setBio(bio);
        user.setLivingHabits(livingHabits);
        user.setInterests(interests);
        user.setPassword(hashedPassword); 
        user.setGender(gender);

        return userRepository.save(user);
    }

    public User login(String googleEmail, String password) {
        Optional<User> userOptional = userRepository.findByGoogleEmail(googleEmail);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password."); 
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password."); 
        }

        return user;
    }

    public User createUserFromOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> userInfo = oAuth2User.getAttributes();
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        
        // Handle case where name might be null
        String username = name != null ? name : email.split("@")[0];

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUserName(username);
        
        // Set a default password for OAuth2 users
        String defaultPassword = UUID.randomUUID().toString();
        newUser.setPassword(passwordEncoder.encode(defaultPassword));
        
        return userRepository.save(newUser);
}
    public Optional<User> findByUserName(String username) {
        return userRepository.findByUserName(username);
    }
}
