package com.google.rentit.user.dto;

import org.springframework.web.bind.annotation.CrossOrigin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@CrossOrigin
@AllArgsConstructor
public class UserProfileDto {
    private String userName;
    private String googleEmail;
}