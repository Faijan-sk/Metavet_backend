package com.example.demo.Config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Use setAllowedOriginPatterns instead of setAllowedOrigins for better flexibility
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:*",              // All localhost ports
            "http://34.10.49.96*",             // GCP frontend - all ports
            "http://34.71.120.171*",           // GCP frontend - all ports
            "https://34.71.120.171*",          // GCP frontend HTTPS - all ports
            "http://34.170.68.167*",           // GCP frontend - all ports (FIXED)
            "https://34.170.68.167*",          // GCP frontend HTTPS - all ports
            "https://34.61.254.251*",          // GCP frontend HTTPS - all ports
            "http://35.206.66.49*"             // GCP frontend - all ports
        ));

        // ✅ All HTTP methods allowed
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        // ✅ All headers allowed
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // ✅ Allow credentials (for JWT tokens in headers)
        configuration.setAllowCredentials(true);

        // ✅ Preflight cache for 1 hour
        configuration.setMaxAge(3600L);

        // ✅ Expose Authorization header to frontend
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Apply to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}