package com.nashrookie.lavish.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import com.nashrookie.lavish.dto.request.LoginRequest;
import com.nashrookie.lavish.dto.request.RegisterRequest;
import com.nashrookie.lavish.dto.response.AuthResponse;
import com.nashrookie.lavish.exception.RefreshTokenInvalidException;
import com.nashrookie.lavish.service.JwtService;
import com.nashrookie.lavish.service.UserService;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

// TODO: Add mechanism to revoke token.
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private static final String COOKIE_NAME = "refresh";
    private static final String PATH_ONLY = "/refresh";
    private static final Integer REFRESH_TOKEN_MAXAGE = 60 * 60 * 24; // Seconds. Must sync with JwtService

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse auth = userService.verify(request);
        String refreshToken = jwtService.generateRefreshToken(auth.username(), auth.roles());
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, refreshToken).path(PATH_ONLY).httpOnly(true)
                .maxAge(REFRESH_TOKEN_MAXAGE).build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(auth);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse auth = userService.register(request);
        String refreshToken = jwtService.generateRefreshToken(auth.username(), auth.roles());
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, refreshToken).path(PATH_ONLY).httpOnly(true)
                .maxAge(REFRESH_TOKEN_MAXAGE).build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(auth);
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue(required = false) String refresh) {
        if (refresh == null) {
            throw new RefreshTokenInvalidException();
        }
        List<String> roles = jwtService.getStringRoleList(refresh);
        String username = jwtService.validateRefreshToken(refresh);
        String token = jwtService.generateAccessToken(username, roles);
        AuthResponse response = AuthResponse.builder().username(username).roles(roles).accessToken(token).build();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .path(PATH_ONLY)
                .httpOnly(true)
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Logged out");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("TEST");
    }
}
