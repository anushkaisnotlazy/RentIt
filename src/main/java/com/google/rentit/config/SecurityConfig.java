package com.google.rentit.config; 


import java.lang.reflect.Array;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@CrossOrigin

@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtService jwtService;

    @Bean
    @Order(0)
    public SecurityFilterChain filterChainIgnoreAuth(HttpSecurity http) throws Exception {
        
        http.securityMatcher("/api/auth/**", "/error", "/properties/**", "/users/**").authorizeHttpRequests((authorize) -> authorize
                                    .requestMatchers("/api/auth/**", "/error", "/properties/**", "/users/**").permitAll())
                                    .cors(Customizer.withDefaults())
                                    .csrf((csrf) -> csrf.disable());
        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/**").authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(myJwtDecoder())))
                .cors(Customizer.withDefaults())
                .csrf((csrf) -> csrf.disable());
        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource myCorsConfig() {
        var config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://127.0.0.1:3000");
        config.addAllowedOrigin("http://127.0.0.1:3000");
        config.setAllowedMethods(List.of("GET", "PUT", "POST", "OPTIONS", "DELETE"));
        config.addExposedHeader("Token-Status");
        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", config);
        return src;
    }
    @Bean
       public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Specify the exact origins that are allowed to make requests.
        // MUST match your React app's development and production URLs.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173","http://localhost:5174", "http://localhost:3000")); // Your React app's origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
        configuration.setAllowCredentials(true); // Allow sending cookies/auth headers
        configuration.setMaxAge(3600L); // Max age of the CORS pre-flight request in seconds

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this CORS config to all paths
        return source;
    }
    private JwtDecoder myJwtDecoder() {
        if (!(jwtService.getPublicKey() instanceof RSAPublicKey)) {
            return null;
        } else {
            var asRSAKey = (RSAPublicKey) jwtService.getPublicKey();
            System.out.println(asRSAKey);
            return NimbusJwtDecoder.withPublicKey(asRSAKey).signatureAlgorithm(SignatureAlgorithm.RS512).build();
        }
    }

    // private JwtAuthenticationConverter myJwtAuthenticationConverter() {
    //     var jwtAuthenticationConverter = new JwtAuthenticationConverter();
    //     jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
    //         List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    //         var role = jwt.getClaimAsBoolean("isAdmin") ? "ROLE_ADMIN" : "ROLE_USER";
    //         grantedAuthorities.add(new SimpleGrantedAuthority(role));
    //         return grantedAuthorities;
    //     });
    //     return jwtAuthenticationConverter;
    // }
}
