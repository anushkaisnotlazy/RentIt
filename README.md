# Rent-It Backend Service

This repository contains the backend source code for the "Rent-It" application. It is a robust RESTful API built with Java and the Spring Boot framework, designed to handle user authentication, property listings, and data management for a rental platform.

## Table of Contents
1.  [Overview](#overview)
2.  [Key Features](#key-features)
3.  [Technologies Used](#technologies-used)
4.  [Project Structure](#project-structure)
5.  [Prerequisites](#prerequisites)
6.  [Setup and Configuration](#setup-and-configuration)
7.  [Running the Application](#running-the-application)
8.  [API Endpoint Specifications](#api-endpoint-specifications)

## Overview

The Rent-It backend provides a complete set of services for a property rental application. It manages users, handles secure authentication via both traditional email/password and Google OAuth2, and exposes endpoints for creating, reading, updating, and deleting (CRUD) property listings. It is designed to be a secure, scalable, and maintainable foundation for a modern web application.

## Key Features

*   **User Authentication**: Secure user signup and login using JWT (JSON Web Tokens).
*   **OAuth2 Integration**: Allows users to sign in or sign up using their Google accounts.
*   **User Profile Management**: Endpoints to create, read, update, and delete user profiles.
*   **Property Listings**: Full CRUD functionality for rental properties.
*   **Advanced Filtering**: Filter properties by location (country, state, city), type, and price range.
*   **Centralized Configuration**: Unified handling of security, CORS, and application settings.
*   **Global Exception Handling**: Graceful error handling for a better developer and user experience.

## Technologies Used

*   **Language**: Java 17+
*   **Framework**: Spring Boot 3.x
*   **Security**: Spring Security (JWT, OAuth2 Client, Password Encoding)
*   **Data Persistence**: Spring Data JPA / Hibernate
*   **Database**: Designed for a relational database like PostgreSQL or MySQL.
*   **API**: Spring Web (REST controllers)
*   **Authentication**: JSON Web Tokens (JWT) via the `io.jsonwebtoken` library.
*   **Validation**: Jakarta Bean Validation for request data integrity.
*   **Boilerplate Reduction**: Lombok
*   **Build Tool**: Maven or Gradle

## Project Structure

The project follows a standard feature-driven package structure to ensure clean separation of concerns.

```
com.google.rentit
├── RentitApplication.java      # Main Spring Boot application class
│
├── auth                        # Authentication-related code
│   ├── controller              # AuthController, OAuthController
│   └── service                 # AuthService
│
├── common
│   └── enums                   # Reusable enums (PropertyType, Role, etc.)
│
├── config                      # Central configuration files
│   ├── CorsConfig.java         # Global CORS settings
│   ├── JwtService.java         # JWT generation and validation
│   └── SecurityConfig.java     # Spring Security filter chains and rules
│
├── exception                   # Global exception handling
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
│
├── property                    # Feature package for Properties
│   ├── controller
│   ├── dto                     # Data Transfer Objects for properties
│   ├── model
│   ├── repository
│   └── service
│
└── user                        # Feature package for Users
    ├── controller
    ├── dto
    ├── model
    ├── repository
    └── service
```

## Prerequisites

Before you begin, ensure you have the following installed:
*   **JDK 17** or newer.
*   **Maven** or **Gradle**.
*   A running instance of a relational database (e.g., **PostgreSQL**, **MySQL**).
*   **Google OAuth2 Credentials** (Client ID and Client Secret) from the Google Cloud Console.

## Setup and Configuration

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd <repository-directory>
    ```

2.  **Configure the application:**
    Open the `src/main/resources/application.properties` (or `application.yml`) file. You will need to add configuration for your database and Google OAuth2 credentials.

    **Example `application.properties`:**
    ```properties
    # Database Configuration (Example for PostgreSQL)
    spring.datasource.url=jdbc:postgresql://localhost:5432/rentit_db
    spring.datasource.username=your_db_user
    spring.datasource.password=your_db_password
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

    # Google OAuth2 Client Configuration
    spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
    spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
    spring.security.oauth2.client.registration.google.scope=profile,email
    spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/api/auth/oauth/callback
    ```

## Running the Application

You can run the application using your IDE (like IntelliJ or VS Code) or from the command line.

**Using Maven:**
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```
The server will start on `http://localhost:8080` by default.

## API Endpoint Specifications

Here is a summary of the primary API endpoints.

#### Authentication (`/api/auth`)
*   `POST api/auth/signup`: Registers a new user with email, password, and profile details.
*   `POST api/auth/login`: Authenticates a user and returns a JWT access token and a refresh token in an HttpOnly cookie.
*   `GET /oauth/callback`: The redirect URI for the Google OAuth2 flow. It handles user creation/login and redirects to the frontend with a JWT.

#### OAuth Flow (`/`)
*   `GET /oauth2/authorize/google`: Initiates the Google OAuth2 login flow by redirecting the user to Google's consent screen.

#### User Management (`/users`)
*   `GET /{id}`: Retrieves the public profile of a user by their ID.
*   `POST /createprofile`: Creates a new user profile (alternative to `/signup`).
*   `PUT /{id}`: Updates the profile information for a given user.
*   `DELETE /{id}`: Deletes a user account.

#### Property Management (`/properties`)
*   `GET /all`: Retrieves a list of all properties.
*   `POST /add`: Creates a new property listing.
*   `PUT /add/{id}`: Updates an existing property listing by its ID.
*   `GET /filter`: Filters properties based on query parameters.
    *   **Query Params**: `country`, `state`, `city`, `propertyType`, `minPrice`, `maxPrice`.
    *   **Example**: `/properties/filter?city=New York&maxPrice=3000`
