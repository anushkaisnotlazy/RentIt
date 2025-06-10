package com.google.rentit.user.model;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.google.rentit.common.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

// @NoArgsConstructor 
// @AllArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Google ID cannot be blank")
    @Size(max = 255, message = "Google ID must be at most 255 characters")
    @Column(name = "google_id", nullable = false, unique = true)
    private String googleId;

    @NotBlank(message = "Google email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Google email must be at most 255 characters")
    @Column(name = "google_email", nullable = false, unique = true)
    private String googleEmail;

    @NotBlank(message = "User name cannot be blank")
    @Size(max = 100, message = "First name must be at most 100 characters")
    @Column(name = "user_name", nullable = false)
    private String userName;

    // @Size(max = 2048, message = "Profile picture URL must be at most 2048 characters")
    // @Column(name = "profile_picture_url")
    // private String profilePictureUrl;

    @Size(max = 1000, message = "Bio must be at most 1000 characters")
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @NotNull(message = "Role cannot be null")
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    // JSONB type for flexible living habits
    // @JdbcTypeCode is used for Hibernate 6+ to explicitly map JSONB
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "living_habits", columnDefinition = "jsonb")
    private String livingHabits; // Example: {"cleanliness": "very clean", "noise": "quiet"}

    // // JSONB type for flexible interests
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "interests", columnDefinition = "jsonb")
    private String interests; // Example: {"hobbies": "reading", "music_genre": "jazz"}

    @PositiveOrZero(message = "Preferred radius must be positive or zero")
    @Column(name = "preferred_radius_km")
    private Double preferredRadiusKm;

    @Size(max = 10, message = "Pincode must be at most 10 characters")
    @Column(name = "preferred_location_pincode")
    private String preferredLocationPincode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @NotBlank(message = "Password cannot be blank")
    @Size(max = 255, message = "Password must be at most 255 characters")
    @Column(name = "password", nullable = false)
    private String password;

    

    public String getPhoneNumber() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getPhoneNumber'");
        return phoneNumber;
    }

    public String getEmail() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getPhoneNumber'");
        return googleEmail;
    }

    public void setEmail(String googleEmail) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'setPhoneNumber'");
        this.googleEmail
        = googleEmail;
    
    }

    public String getUserName() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getPhoneNumber'");
        return userName;
    }

    public void setUserName(String userName) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'setPhoneNumber'");
        this.userName = userName;
    
    }

    public void setPhoneNumber(String phoneNumber) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'setPhoneNumber'");
        this.phoneNumber = phoneNumber;
    
    }

    public void setPassword(String password) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'setPassword'");
        this.password = password;
    }

    public String getPassword() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getPassword'");
        return password;
    }
}
