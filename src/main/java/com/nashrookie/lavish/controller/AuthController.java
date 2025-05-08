package com.nashrookie.lavish.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private static final String COOKIE_NAME = "refresh";
    private static final String REFRESH_ENDPOINT = "/refresh";
    private static final String LOGOUNT_ENDPOINT = "/logout";

    private Integer refreshTokenMaxage;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public void setRefreshTokenMaxage(@Value("${application.jwt.refresh-lifetime}") Integer refreshTokenMaxage) {
        // The value in properties file is in milliseconds. We need to convert to
        // seconds since maxAge in ResponseCookie is in seconds.
        this.refreshTokenMaxage = refreshTokenMaxage / 1000;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse auth = userService.verify(request);
        String refreshToken = jwtService.generateRefreshToken(auth.id(), auth.username(), auth.roles());
        ResponseCookie refreshPathCookie = this.createCookie(refreshToken, REFRESH_ENDPOINT, refreshTokenMaxage);
        ResponseCookie logoutPathCookie = this.createCookie(refreshToken, LOGOUNT_ENDPOINT, refreshTokenMaxage);
        log.info("Refresh token: {}", refreshToken);
        log.info("Access token: {}", auth.accessToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshPathCookie.toString(), logoutPathCookie.toString())
                .body(auth);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse auth = userService.register(request);
        String refreshToken = jwtService.generateRefreshToken(auth.id(), auth.username(), auth.roles());
        ResponseCookie refreshPathCookie = this.createCookie(refreshToken, REFRESH_ENDPOINT, refreshTokenMaxage);
        ResponseCookie logoutPathCookie = this.createCookie(refreshToken, LOGOUNT_ENDPOINT, refreshTokenMaxage);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshPathCookie.toString(), logoutPathCookie.toString())
                .body(auth);
    }

    @GetMapping(REFRESH_ENDPOINT)
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue(required = false) String refresh) {
        if (refresh == null) {
            log.warn("Refresh token is null");
            throw new RefreshTokenInvalidException();
        }

        String username = jwtService.validateRefreshToken(refresh);
        List<String> roles = jwtService.getStringRoleList(refresh);
        Long id = jwtService.getId(refresh);

        String token = jwtService.generateAccessToken(id, username, roles);
        AuthResponse response = AuthResponse.builder().username(username).roles(roles).accessToken(token).build();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping(LOGOUNT_ENDPOINT)
    public ResponseEntity<String> logout(@CookieValue(required = true) String refresh) {
        ResponseCookie refreshPathCookie = this.createCookie("", REFRESH_ENDPOINT, 0);
        ResponseCookie logoutPathCookie = this.createCookie("", LOGOUNT_ENDPOINT, 0);
        jwtService.revokeToken(refresh);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshPathCookie.toString(), logoutPathCookie.toString())
                .body("Logged out");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {        
        return ResponseEntity.ok("TEST");
    }

    private ResponseCookie createCookie(String token, String path, Integer maxAge) {
        return ResponseCookie.from(COOKIE_NAME, token).path(path).httpOnly(true)
                .maxAge(maxAge).build();
    }
}
