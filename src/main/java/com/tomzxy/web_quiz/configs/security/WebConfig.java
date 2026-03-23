package com.tomzxy.web_quiz.configs.security;

import com.tomzxy.web_quiz.containts.ApiDefined;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebConfig {
        private final JWTFilter jwtFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http
                                .cors(cors -> {
                                })
                                .csrf(AbstractHttpConfigurer::disable)

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                // Swagger
                                                .requestMatchers(
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                // Auth APIs
                                                .requestMatchers(
                                                                "/api/auth/login",
                                                                "/api/auth/signup",
                                                                "/api/auth/refresh",
                                                                "/api/auth/logout")
                                                .permitAll()

                                                // Public quiz APIs
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/quizzes/**",
                                                                "/api/subjects/**")
                                                .permitAll()

                                                // Admin APIs
                                                .requestMatchers("/api/tomzxyadmin/**")
                                                .hasRole("ADMIN")

                                                // Creator APIs - Allow authenticated users, let @PreAuthorize handle
                                                // permissions
                                                .requestMatchers(HttpMethod.POST, "/api/quizzes/**")
                                                .authenticated()

                                                .requestMatchers(HttpMethod.PUT, "/api/quizzes/**")
                                                .authenticated()

                                                .requestMatchers(HttpMethod.DELETE, "/api/quizzes/**")
                                                .authenticated()

                                                // Other APIs
                                                .anyRequest().authenticated())

                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowCredentials(true);
                configuration.setAllowedOrigins(
                                List.of("http://localhost:8081","http://localhost:5173", // Port mặc định của Vite
                                        "https://*.vercel.app",   // Cho phép tất cả subdomain của vercel (tiện cho preview)
                                        "https://quizory-*.vercel.app"));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}