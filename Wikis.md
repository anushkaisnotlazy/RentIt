# Rent-It Backend: Architecture & API Documentation

Welcome to the Rent-It Backend Wiki. This document provides a deep dive into the system's architecture, security implementation, and a complete reference for all available API endpoints.

## Table of Contents
1.  [System Architecture](#1-system-architecture)
    *   [Layered Architecture](#layered-architecture)
    *   [Security Architecture](#security-architecture)
    *   [Data Model](#data-model)
    *   [Configuration](#configuration)
    *   [Exception Handling](#exception-handling)
2.  [Authentication Flows](#2-authentication-flows)
    *   [Standard JWT Login Flow](#standard-jwt-login-flow)
    *   [Google OAuth2 Login Flow](#google-oauth2-login-flow)
3.  [API Endpoint Documentation](#3-api-endpoint-documentation)
    *   [Authentication Endpoints](#authentication-endpoints)
    *   [User Endpoints](#user-endpoints)
    *   [Property Endpoints](#property-endpoints)

---

## 1. System Architecture

The backend is built using the **Spring Boot** framework, following best practices for creating scalable and maintainable RESTful web services.

### Layered Architecture

The application is structured in a classic three-layer architecture to ensure a clean separation of concerns:

*   **Controller Layer (`*.controller.*`)**: This is the entry point for all API requests. Its sole responsibility is to receive HTTP requests, delegate them to the appropriate service, and format the response. It handles web-specific tasks like parsing request bodies and path variables.
*   **Service Layer (`*.service.*`)**: This layer contains the core business logic of the application. It orchestrates operations, validates data, enforces business rules (e.g., "a user cannot sign up with an existing email"), and coordinates with the repository layer.
*   **Repository Layer (`*.repository.*`)**: This layer is responsible for all data access and persistence. It consists of interfaces extending Spring Data JPA's `JpaRepository`, which abstracts away the boilerplate code for database operations (CRUD, filtering, etc.).

### Security Architecture

Security is a critical component, managed by **Spring Security**.

*   **Configuration (`SecurityConfig.java`)**: Two security filter chains are configured:
    1.  **Public Endpoints (`@Order(0)`):** A chain that explicitly permits access to authentication (`/api/auth/**`), public data (`/properties/**`, `/users/**`), and error endpoints without requiring a token.
    2.  **Authenticated Endpoints (`@Order(1)`):** A chain that secures all other endpoints (`/**`). It requires a valid JWT Bearer token in the `Authorization` header for access.

*   **JWT (JSON Web Tokens)**:
    *   The `JwtService` is responsible for creating and validating tokens.
    *   It uses an **asymmetric RS512 algorithm** (RSA with SHA-512), which is highly secure as the signing key (private key) is never exposed. Only the public key is used for verification.
    *   **Access Tokens**: Short-lived (30 seconds) tokens used to access protected resources. Their short lifespan minimizes the risk if one is compromised.
    *   **Refresh Tokens**: Long-lived (15 hours) tokens stored in a secure, `HttpOnly` cookie. They are used to obtain new access tokens without requiring the user to log in again.

*   **OAuth2 Client**:
    *   The backend is configured as an OAuth2 client for Google.
    *   It handles the authorization code flow, securely exchanging the code provided by Google for user information and then issuing its own JWTs to the frontend client.

### Data Model

The core data is represented by two main JPA entities:

*   **`User.java`**: Represents a user of the platform. It stores profile information, credentials (hashed password), and contact details.
*   **`Property.java`**: Represents a rental property. It contains detailed information about the listing, including location, price, type, and amenities. It holds a many-to-one relationship with the `User` entity, linking each property to its owner (`renter`).

### Configuration

*   **`application.properties`**: The primary file for external configuration, including database connection details and Google OAuth2 client credentials.
*   **`CorsConfig.java`**: A dedicated class to define global Cross-Origin Resource Sharing (CORS) rules, ensuring that frontend applications hosted on different origins (e.g., `http://localhost:3000`) can securely interact with the API.

### Exception Handling

*   **`GlobalExceptionHandler.java`**: A centralized mechanism using `@ControllerAdvice` to catch and handle exceptions across the entire application.
    *   It translates specific exceptions (`ResourceNotFoundException`, `MethodArgumentNotValidException`) into structured, user-friendly JSON error responses with appropriate HTTP status codes (e.g., 404, 400).
    *   This prevents stack traces from being exposed to the client and provides consistent error reporting.

---

## 2. Authentication Flows

### Standard JWT Login Flow

1.  **Client**: Sends a `POST` request to `/api/auth/login` with user's `googleEmail` and `password`.
2.  **Server (`AuthController` -> `AuthService`)**:
    *   Verifies the credentials against the hashed password in the database.
    *   If valid, it calls `JwtService`.
3.  **Server (`JwtService`)**:
    *   Generates a short-lived **Access Token**.
    *   Generates a long-lived **Refresh Token**.
4.  **Server (`AuthController`)**:
    *   Returns the **Access Token** and user data in the JSON response body.
    *   Sets the **Refresh Token** in a secure, `HttpOnly` cookie in the response headers.
5.  **Client**: Stores the Access Token in memory and includes it in the `Authorization: Bearer <token>` header for all subsequent requests to protected endpoints.

### Google OAuth2 Login Flow

1.  **Client**: User clicks a "Login with Google" button, which directs them to the backend endpoint `GET /oauth2/authorize/google`.
2.  **Server (`OAuthController`)**: Redirects the user to Google's authentication and consent screen.
3.  **Google**: After the user authenticates and grants permission, Google redirects the user back to the `redirect-uri` configured in `application.properties` (`/api/auth/oauth/callback`), including an authorization `code`.
4.  **Server (Spring Security)**: Intercepts the callback, validates the state, and securely exchanges the `code` with Google for an access token and the user's profile information. It creates an `OAuth2User` principal.
5.  **Server (`AuthController.oauthSuccess`)**:
    *   Receives the `OAuth2User` principal.
    *   Checks if a user with that email already exists in the database. If not, it creates a new `User` record.
    *   Generates JWT Access and Refresh tokens for this user.
6.  **Server**: Redirects the user back to a frontend-specific URL (e.g., `http://localhost:3000/oauth/callback`), passing the **Access Token** as a URL query parameter. It also sets the Refresh Token cookie.
7.  **Client**: The frontend page at the callback URL captures the token from the URL, stores it, and completes the login process.

---

## 3. API Endpoint Documentation

### Authentication Endpoints

These endpoints are responsible for user registration and login. They are publicly accessible.

---

#### Register a New User
*   **Endpoint:** `POST /api/auth/signup`
*   **Description:** Creates a new user account.
*   **Authentication:** None.
*   **Request Body (`SignupRequest`):**
    ```json
    {
      "googleEmail": "new.user@example.com",
      "userName": "NewUser123",
      "password": "A-very-strong-password!123",
      "bio": "I am a software engineer looking for a quiet place.",
      "livingHabits": "Clean, non-smoker",
      "interests": "Hiking, board games",
      "phoneNumber": "555-123-4567",
      "gender": "FEMALE"
    }
    ```
*   **Success Response (201 Created):**
    *   Returns the newly created `User` object (password field will be hashed, not plaintext).
    ```json
    {
        "id": 101,
        "googleEmail": "new.user@example.com",
        "userName": "NewUser123",
        "bio": "I am a software engineer looking for a quiet place.",
        "livingHabits": "Clean, non-smoker",
        "interests": "Hiking, board games",
        "phoneNumber": "555-123-4567",
        "gender": "FEMALE",
        "password": "{bcrypt}$2a$10$...", // Hashed password
        "email": "new.user@example.com"
    }
    ```
*   **Error Responses:**
    *   `400 Bad Request`: If the email already exists or if request body validation fails (e.g., missing fields).

---

#### User Login
*   **Endpoint:** `POST /api/auth/login`
*   **Description:** Authenticates a user and returns JWTs.
*   **Authentication:** None.
*   **Request Body (`LoginRequest`):**
    ```json
    {
      "googleEmail": "new.user@example.com",
      "password": "A-very-strong-password!123"
    }
    ```
*   **Success Response (200 OK):**
    *   The `Set-Cookie` header will contain the `refreshToken`.
    *   The response body (`LoginResponse`) contains the access token and user data.
    ```json
    {
        "jwt": "eyJhbGciOiJSUzUxMiJ9...", // Access Token
        "message": "Login Successful!!",
        "user": {
            "id": 101,
            "googleEmail": "new.user@example.com",
            "userName": "NewUser123",
            // ... other user fields
        }
    }
    ```
*   **Error Responses:**
    *   `401 Unauthorized`: If the email or password is invalid.

---

### User Endpoints

Endpoints for managing user profiles. Publicly readable, but modification/deletion would typically require authentication in a production scenario (though currently permitted by `SecurityConfig`).

---

#### Get User Profile
*   **Endpoint:** `GET /users/{id}`
*   **Description:** Retrieves a user's public profile information by their ID.
*   **Authentication:** None.
*   **Success Response (200 OK):**
    ```json
    {
        "userName": "NewUser123",
        "googleEmail": "new.user@example.com"
    }
    ```
*   **Error Responses:**
    *   `404 Not Found`: If no user exists with the given ID.

---

#### Update User Profile
*   **Endpoint:** `PUT /users/{id}`
*   **Description:** Updates an existing user's profile.
*   **Authentication:** Requires JWT Bearer Token (as it's not explicitly permitted).
*   **Request Body (`User` model subset):**
    ```json
    {
      "googleEmail": "new.user.updated@example.com",
      "userName": "UpdatedUser",
      "bio": "My updated bio.",
      "livingHabits": "Still clean",
      "interests": "Now into pottery",
      "phoneNumber": "555-987-6543",
      "gender": "NON_BINARY"
    }
    ```
*   **Success Response (200 OK):**
    ```json
    {
        "userName": "UpdatedUser",
        "googleEmail": "new.user.updated@example.com"
    }
    ```
*   **Error Responses:**
    *   `404 Not Found`: If no user exists with the given ID.

---

#### Delete User
*   **Endpoint:** `DELETE /users/{id}`
*   **Description:** Deletes a user account.
*   **Authentication:** Requires JWT Bearer Token.
*   **Success Response (204 No Content):** An empty response body.
*   **Error Responses:**
    *   `404 Not Found`: If no user exists with the given ID.

---

### Property Endpoints

Endpoints for creating and managing property listings. Publicly accessible as per `SecurityConfig`.

---

#### Get All Properties
*   **Endpoint:** `GET /properties/all`
*   **Description:** Retrieves a list of all property listings.
*   **Authentication:** None.
*   **Success Response (200 OK):**
    *   Returns a JSON array of `Property` objects.
    ```json
    [
        {
            "id": 1,
            "renter": { "id": 101, "userName": "NewUser123", ... },
            "propertyType": "APARTMENT",
            "title": "Cozy Downtown Apartment",
            "address": "123 Main St, Anytown, USA",
            "rentMonthly": 2500.0,
            "numberOfBedrooms": 2,
            "numberOfBathrooms": 1,
            "isAvailableForRent": true,
            "country": "USA",
            "state": "California",
            "city": "Anytown",
            "listingType": "ENTIRE_PLACE",
            "lookingFor": "ANY"
            // ... other fields
        }
    ]
    ```

---

#### Add a New Property
*   **Endpoint:** `POST /properties/add`
*   **Description:** Creates a new property listing.
*   **Authentication:** Requires JWT Bearer Token.
*   **Request Body (`PropertyCreationDTO`):**
    ```json
    {
        "renterId": 101,
        "propertyType": "APARTMENT",
        "title": "Spacious Suburban House",
        "description": "A beautiful house with a large backyard.",
        "address": "456 Oak Ave, Suburbia, USA",
        "rentMonthly": 3200.0,
        "numberOfBedrooms": 4,
        "numberOfBathrooms": 3,
        "amenities": "[\"Parking\", \"Dishwasher\", \"Garden\"]",
        "availabilityStartDate": "2024-09-01T00:00:00.000+00:00",
        "isAvailableForFlatmates": false,
        "isAvailableForRent": true,
        "country": "USA",
        "state": "Texas",
        "city": "Suburbia",
        "listingType": "ENTIRE_PLACE",
        "lookingFor": "ANY"
    }
    ```
*   **Success Response (200 OK):**
    *   Returns the full `Property` object that was created.
*   **Error Responses:**
    *   `400 Bad Request`: If validation fails (e.g., `renterId` is null).
    *   `404 Not Found`: If the `renterId` does not correspond to an existing user.

---

#### Edit a Property
*   **Endpoint:** `PUT /properties/add/{id}`
*   **Description:** Updates an existing property listing.
*   **Authentication:** Requires JWT Bearer Token.
*   **Request Body (`PropertyCreationDTO`):** Same as the add endpoint.
*   **Success Response (200 OK):** Returns the updated `Property` object.
*   **Error Responses:**
    *   `404 Not Found`: If no property with the given `{id}` exists.

---

#### Filter Properties
*   **Endpoint:** `GET /properties/filter`
*   **Description:** Retrieves a list of properties matching the filter criteria.
*   **Authentication:** None.
*   **Query Parameters:**
    *   `country` (String, optional)
    *   `state` (String, optional)
    *   `city` (String, optional)
    *   `propertyType` (String, optional, e.g., "APARTMENT", "ROOM")
    *   `minPrice` (Double, optional)
    *   `maxPrice` (Double, optional)
*   **Example Request:**
    `GET /properties/filter?city=Suburbia&maxPrice=3500`
*   **Success Response (200 OK):**
    *   Returns a JSON array of `Property` objects that match the filters.