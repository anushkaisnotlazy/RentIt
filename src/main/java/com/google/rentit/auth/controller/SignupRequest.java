package com.google.rentit.auth.controller;

import com.google.rentit.common.enums.Role;

public record SignupRequest(String googleEmail, String userName, Role role, String bio, String livingHabits, String interests,
                       String googleId, Double preferredRadiusKm, String preferredLocationPincode, String phoneNumber, String password) {
    
}
