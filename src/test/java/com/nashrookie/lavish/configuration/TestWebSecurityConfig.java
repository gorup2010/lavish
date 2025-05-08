package com.nashrookie.lavish.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class TestWebSecurityConfig {

    private static final String[] publicEndpoints = { "/login", "/register", "/refresh", "/test/**",
            "/swagger-ui.html", "/swagger-ui/**", "/api-docs/**" };

    private static final String[] onlyGetEndpoints = { "/api/v1/categories/**", "/api/v1/ratings/**",
            "/api/v1/products/**" };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .addFilterBefore(new CustomFilter(), LogoutFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(publicEndpoints).permitAll()
                        .requestMatchers(HttpMethod.GET, onlyGetEndpoints).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout.disable())
                .build();
    }

}
