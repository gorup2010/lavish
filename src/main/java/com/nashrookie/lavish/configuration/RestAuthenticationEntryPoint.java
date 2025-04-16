package com.nashrookie.lavish.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashrookie.lavish.dto.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ErrorResponse apiResponse = ErrorResponse.builder().code(HttpStatus.FORBIDDEN.value()).message(authException.getMessage()).build();
        ObjectMapper objectMapper = new ObjectMapper();

        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}

