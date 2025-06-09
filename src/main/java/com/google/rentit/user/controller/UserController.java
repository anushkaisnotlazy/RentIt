package com.google.rentit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.rentit.user.dto.UserProfileDto;
import com.google.rentit.user.model.User;
import com.google.rentit.user.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {


    @Autowired
    private UserService userService;

    // public UserController(UserService userService) {
    //     this.userService = userService;
    // }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserProfileDto dto = new UserProfileDto(user.getUserName(), user.getGoogleEmail());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserProfileDto> updateUser (@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        UserProfileDto dto = new UserProfileDto(updatedUser.getUserName(), updatedUser.getPhoneNumber());
        return ResponseEntity.ok(dto);

    }

}