package com.google.rentit.user.model;

import org.springframework.web.bind.annotation.CrossOrigin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

// @NoArgsConstructor
// @AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@CrossOrigin
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Google email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Google email must be at most 255 characters")
    @Column(name = "google_email", nullable = false, unique = true)
    private String googleEmail;

    @NotBlank(message = "User name cannot be blank")
    @Size(max = 100, message = "First name must be at most 100 characters")
    @Column(name = "user_name", nullable = false)
    private String userName;

    @Size(max = 1000, message = "Bio must be at most 1000 characters")
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "living_habits")
    private String livingHabits; // Example: {"cleanliness": "very clean", "noise": "quiet"}

    @Column(name = "interests")
    private String interests; // Example: {"hobbies": "reading", "music_genre": "jazz"}

    @Column(name = "phone_number")
    private String phoneNumber;

    @NotBlank(message = "Password cannot be blank")
    @Size(max = 255, message = "Password must be at most 255 characters")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "gender")
    private String gender;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return googleEmail;
    }

    public void setEmail(String googleEmail) {
        this.googleEmail = googleEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}