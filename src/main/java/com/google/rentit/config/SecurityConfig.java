package com.google.rentit.config; 


import java.security.interfaces.RSAPublicKey;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
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
        config.setAllowedMethods(List.of("GET", "PUT", "POST", "OPTIONS", "DELETE"));
        config.addExposedHeader("Token-Status");
        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", config);
        return src;
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
