package com.examly.springapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger and OpenAPI docs
                .requestMatchers(
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/error"
                ).permitAll()

                // Authentication endpoints
                .requestMatchers("/api/auth/**").permitAll()

                // Public Pet Endpoints
                .requestMatchers(HttpMethod.GET, "/api/pets").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/pets/{id:[0-9]+}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/pets/{id:[0-9]+}/images").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/pets/most-viewed").permitAll()

                // Public Adoption Request Submission
                .requestMatchers(HttpMethod.POST, "/api/adoption-requests").permitAll()

                // Public Shelter details
                .requestMatchers(HttpMethod.GET, "/api/shelter").permitAll()

                // Public Donation endpoints
                .requestMatchers("/api/donations/**").permitAll()

                // User Favorites endpoints
                .requestMatchers("/api/favorites/**").hasAnyRole("USER", "STAFF", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/adoption-requests/my-requests").hasAnyRole("USER", "STAFF", "ADMIN")

                // Staff / Admin Only Endpoints
                .requestMatchers(HttpMethod.POST, "/api/pets/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/pets/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/pets/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/pets/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers("/api/pets/staff/**").hasAnyRole("STAFF", "ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/adoption-requests").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/adoption-requests/{id:[0-9]+}").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/adoption-requests/{id:[0-9]+}/status").hasAnyRole("STAFF", "ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/shelter").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers("/api/analytics/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers("/api/staff/**").hasAnyRole("STAFF", "ADMIN")

                // Any other requests require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
