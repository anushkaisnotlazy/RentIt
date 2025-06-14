package com.google.rentit.auth.controller;

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
import com.google.rentit.user.service.UserService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private final AuthService authService;

    @Autowired
    private UserService userService;


    @Autowired
    private JwtService jwtService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            User newUser = authService.signUp(
                signUpRequest.googleEmail(),
                signUpRequest.userName(),
                signUpRequest.role(),
                signUpRequest.bio(),
                signUpRequest.livingHabits(),
                signUpRequest.interests(),
                signUpRequest.googleId(),
                signUpRequest.preferredRadiusKm(),
                signUpRequest.preferredLocationPincode(),
                signUpRequest.phoneNumber(),
                signUpRequest.password()
            );
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
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
            response.addCookie(cookie);
            return new ResponseEntity<LoginResponse>(new LoginResponse(accessToken, "Login Successful!!", authenticatedUser), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<LoginResponse>(new LoginResponse("invalid","Invalid email or password!!!", null), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<LoginResponse>(new LoginResponse("invalid","An unexpected error occurred during login.", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/oauth/callback")
    public void oauthSuccess(@AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) throws IOException, java.io.IOException {
        try {
            Map<String, Object> userInfo = principal.getAttributes();
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");

            // logger.info("OAuth2 callback for email: {}", email);

            // Check if user exists or create new user
             Optional<User> maybeUser = authService.findByUserName(name);

            User user;
            
            if (maybeUser.isPresent()) {
                user = maybeUser.get();
                // logger.info("Existing user found: {}", user.getUsername());
            } else {
                user = authService.createUserFromOAuth2User(principal);
                // logger.info("New user created: {}", user.getUsername());
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
            // logger.info("Redirecting to: {}", redirectUrl);
            response.sendRedirect(redirectUrl);
            
        } catch (Exception e) {
            // logger.error("OAuth2 callback failed: {}", e.getMessage(), e);
            response.sendRedirect("http://localhost:5173/login?error=oauth_failed");
        }
    }
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
            String username = jwtService.getUsernameFromToken(jwt);
            
            if (username != null) {
                Optional<User> user = authService.findByUserName(username);
                if (user.isPresent()) {
                    return new ResponseEntity<>(user.get(), HttpStatus.OK);
                }
            }
            
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // logger.error("Get current user failed: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}

