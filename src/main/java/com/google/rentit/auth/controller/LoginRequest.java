package com.google.rentit.auth.controller;

public record LoginRequest(String googleEmail, String password) {
    
}
