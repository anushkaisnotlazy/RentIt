package com.google.rentit.auth.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.rentit.auth.service.AuthService;
import com.google.rentit.config.JwtService;
import com.google.rentit.user.model.User;
import com.google.rentit.user.repository.UserRepository;
import com.google.rentit.user.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;


// Assuming SignupRequest, LoginRequest, and LoginResponse are defined elsewhere in your project
// You'll need to ensure they are properly imported if they are in different packages.
// For example:
// import com.google.rentit.auth.request.SignupRequest;
// import com.google.rentit.auth.request.LoginRequest;
// import com.google.rentit.auth.response.LoginResponse;

@RestController
@CrossOrigin // Keep CrossOrigin if you need it at the controller level for all methods
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
        
    @Autowired
    private final AuthService authService; // Final for constructor injection

    // Removed @Autowired private UserRepository userRepository;
    // Removed @Autowired private UserService userService;

    // private final JwtService jwtService; // Final for constructor injection

    // Use constructor injection for all dependencies
    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            User newUser = authService.signUp(
                signUpRequest.googleEmail(),
                signUpRequest.userName(),
                signUpRequest.bio(),
                signUpRequest.livingHabits(),
                signUpRequest.interests(),
                signUpRequest.phoneNumber(),
                signUpRequest.password(),
                signUpRequest.gender()
            );
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during signup: " + e.getMessage()); // Use err for errors
            return new ResponseEntity<>("An unexpected error occurred during signup.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            User authenticatedUser = authService.login(loginRequest.googleEmail(), loginRequest.password());
            var accessToken = jwtService.createAccessToken(authenticatedUser);
            var refreshToken = jwtService.createRefreshToken(authenticatedUser);

            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/"); // Set path to root to make it accessible everywhere
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            cookie.setSecure(false); // Set to true in production with HTTPS
            response.addCookie(cookie);

            return new ResponseEntity<>(new LoginResponse(accessToken, "Login Successful!!", authenticatedUser), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            System.err.println("Login failed: " + e.getMessage()); // Use err for errors
            return new ResponseEntity<>(new LoginResponse("invalid","Invalid email or password!!!", null), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during login: " + e.getMessage()); // Use err for errors
            return new ResponseEntity<>(new LoginResponse("invalid","An unexpected error occurred during login.", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // REMOVED THE @GetMapping("/oauth/callback") METHOD
    // Its logic is now handled by the AuthenticationSuccessHandler in SecurityConfig.
    @GetMapping("/oauth/callback")
public void oauthSuccess(@AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) throws IOException {
    try {
        Map<String, Object> userInfo = principal.getAttributes();
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        System.out.println("OAuth2 callback for email: " + email); // Added logging

        // Check if user exists or create new user
        Optional<User> maybeUser = userRepository.findByGoogleEmail(email); // Changed to findByGoogleEmail

        User user;
        
        if (maybeUser.isPresent()) {
            user = maybeUser.get();
            System.out.println("Existing user found: " + user.getUserName()); // Added logging
        } else {
            user = authService.createUserFromOAuth2User(principal);
            System.out.println("New user created: " + user.getUserName()); // Added logging
        }

        // Generate JWT token
        String jwt = jwtService.createAccessToken(user);
        
        // Create refresh token and set cookie
        String refreshToken = jwtService.createRefreshToken(user);
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); 
        refreshTokenCookie.setSecure(false); 
        response.addCookie(refreshTokenCookie);
        
       
        String redirectUrl = "http://localhost:5173/oauth/callback?token=" + jwt;
        System.out.println("Redirecting to: " + redirectUrl); // Added logging
        response.sendRedirect(redirectUrl);
        
    } catch (Exception e) {
        System.out.println("OAuth2 callback failed: " + e.getMessage()); // Added logging
        response.sendRedirect("http://localhost:5173/login?error=oauth_failed");
    }
}


    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
            String username = jwtService.getUsernameFromToken(jwt);

            if (username != null) {
                // Assuming findByUserName is in AuthService, or if you need UserRepository, inject it
                Optional<User> user = authService.findByUserName(username); // Using authService
                if (user.isPresent()) {
                    return new ResponseEntity<>(user.get(), HttpStatus.OK);
                }
            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.err.println("Get current user failed: " + e.getMessage()); // Use err for errors
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}