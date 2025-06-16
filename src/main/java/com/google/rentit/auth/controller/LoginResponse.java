package com.google.rentit.auth.controller;

import com.google.rentit.user.model.User;

public record LoginResponse(String jwt, String message, User user) {
    
}
