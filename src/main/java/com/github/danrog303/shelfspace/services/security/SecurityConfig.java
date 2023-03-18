package com.github.danrog303.shelfspace.services.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import com.github.danrog303.shelfspace.errors.ErrorResponse;
import com.github.danrog303.shelfspace.errors.ErrorResponseAdvice;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

/**
 * Configuration for Spring Security library.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled=true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final ObjectMapper jsonMapper;

    @Bean
    public CorsConfigurationSource corsConfiguration() {
        return (request) -> {
            CorsConfiguration config = new CorsConfiguration();
            List<String> allowedOrigins = List.of("http://localhost:4200", "https://shelfspace.danielrogowski.net");
            config.setAllowedOrigins(allowedOrigins);
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            config.setMaxAge(3600L);
            return config;
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfiguration()).and().csrf().disable()
        .authorizeRequests(req -> req
            .antMatchers("/docs/**").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(sess -> sess
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .exceptionHandling(exc -> exc
            .authenticationEntryPoint(unauthorizedEntryPoint())
            .accessDeniedHandler(accessDeniedHandler())
        )
        .oauth2ResourceServer().authenticationEntryPoint(unauthorizedEntryPoint()).jwt();
        return http.build();
    }

    /**
     * Returns custom HTTP 401/Unauthorized response, which corresponds to {@link ErrorResponse}
     * responses produced by {@link ErrorResponseAdvice}.
     */
    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            String errorName = "UNAUTHORIZED";
            String errorMessage = "You are not authorized to access this endpoint. Make sure you passed a valid " +
                    "JWT access token.";
            ErrorResponse errorResponse = new ErrorResponse(errorName, errorMessage);

            response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            jsonMapper.writeValue(response.getWriter(), errorResponse);
        };
    }

    /**
     * Returns custom HTTP 403/Forbidden response, which corresponds to {@link ErrorResponse}
     * responses produced by {@link ErrorResponseAdvice}.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            String errorName = "ACCESS_DENIED";
            String errorMessage = "You do not have access to this resource";
            ErrorResponse errorResponse = new ErrorResponse(errorName, errorMessage);

            response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            jsonMapper.writeValue(response.getWriter(), errorResponse);
        };
    }
}
