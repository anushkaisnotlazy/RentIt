package com.google.rentit.auth.controller;

public record SignupRequest(String googleEmail, String userName, String bio, String livingHabits, String interests,
                    String phoneNumber, String password, String gender) {
    
}
