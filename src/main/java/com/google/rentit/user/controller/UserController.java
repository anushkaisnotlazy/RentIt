package com.google.rentit.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.rentit.user.dto.UserProfileDto;
import com.google.rentit.user.model.User;
import com.google.rentit.user.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserProfileDto dto = new UserProfileDto(user.getFirstName(), user.getLastName(), user.getPhoneNumber());
        return ResponseEntity.ok(dto);
    }

}