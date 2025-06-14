package com.google.rentit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc; // Often helpful, though not strictly required for this specific config

@Configuration // Marks this class as a source of bean definitions and configuration
// @EnableWebMvc // Optional: Can be used to fully control Spring MVC configuration,
              // but Spring Boot often auto-configures it sufficiently.
              // Include if you find your @Override methods aren't taking effect
              // without it, or if you have complex MVC customizations.
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply CORS to all paths in your application
                // CRITICAL: Specify the EXACT origins of your frontend applications.
                // For development:
                .allowedOrigins("http://localhost:3000", "http://localhost:5173","http://localhost:5174")
                // For production: Replace with your actual deployed frontend domain(s)
                // .allowedOrigins("https://your-production-react-app.com")
                // Avoid using "*" in production for allowedOrigins due to security risks.

                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD") // Include HEAD method for completeness
                // OPTIONS method is crucial for CORS pre-flight requests.

                .allowedHeaders("*") // Allow all headers in the request
                // For stricter security, you could list specific headers, e.g.,
                // .allowedHeaders("Authorization", "Content-Type", "X-Requested-With", "Accept")

                .allowCredentials(true) // Crucial if your frontend sends cookies, HTTP authentication,
                                        // or Authorization headers (e.g., Bearer tokens)
                                        // from a different origin.

                .maxAge(3600); // How long the results of a CORS pre-flight request (OPTIONS)
                               // can be cached by the client (in seconds). 3600 = 1 hour.
                               // This reduces the number of pre-flight requests.
    }
}