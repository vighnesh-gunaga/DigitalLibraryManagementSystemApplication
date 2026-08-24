package com.example.DigitalLibraryManagementSystem.config;
import com.example.DigitalLibraryManagementSystem.Security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    // CORS configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5500",
                        "http://127.0.0.1:5500"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }


    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }


    //Security configuration
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // FRONTEND FILES
                        // =========================
                        .requestMatchers(
                                "/HTML/**",
                                "/CSS/**",
                                "/JavaScript/**",
                                "/images/**"
                        ).permitAll()


                        // =========================
                        // AUTHENTICATION
                        // =========================
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password"
                        ).permitAll()


                        // =========================
                        // ADMIN ONLY
                        // =========================

                        // Category management
                        .requestMatchers(
                                "/api/category/**"
                        ).hasRole("ADMIN")

                        // Publisher management
                        .requestMatchers(
                                "/api/publisher/**"
                        ).hasRole("ADMIN")

                        // Book management
                        .requestMatchers(
                                "/api/book/addbook",
                                "/api/book/updatebook/**",
                                "/api/book/deletebook/**"
                        ).hasRole("ADMIN")


                        // =========================
                        // BOOK VIEWING
                        // USER + ADMIN
                        // =========================

                        .requestMatchers(
                                "/api/book/allbooks",
                                "/api/book/book/**",
                                "/api/book/search/**"
                        ).hasAnyRole("USER", "ADMIN")


                        // =========================
                        // ISSUE / RETURN BOOK
                        // USER + ADMIN
                        // =========================

                        .requestMatchers(
                                "/api/issued-books/**"
                        ).hasAnyRole("USER", "ADMIN")


                        // =========================
                        // RESERVATION
                        // USER + ADMIN
                        // =========================

                        .requestMatchers(
                                "/api/reservations/**"
                        ).hasAnyRole("USER", "ADMIN")


                        // =========================
                        // FINE MANAGEMENT
                        // =========================

                        // Admin can manage/view all fines
                        .requestMatchers(
                                "/api/fines/all",
                                "/api/fines/unpaid",
                                "/api/fines/manage/**"
                        ).hasRole("ADMIN")

                        // User can view/manage their own fines
                        .requestMatchers(
                                "/api/fines/user/**"
                        ).hasAnyRole("USER", "ADMIN")


                        // =========================
                        // EVERYTHING ELSE
                        // =========================

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
