package com.nashrookie.lavish.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nashrookie.lavish.dto.request.LoginRequest;
import com.nashrookie.lavish.dto.request.RegisterRequest;
import com.nashrookie.lavish.dto.response.AuthResponse;
import com.nashrookie.lavish.service.UserService;


import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.verify(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    // @PostMapping("/refresh-token")
    // public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequest) {
    //     String refreshToken = refreshTokenRequest.getRefreshToken();
    //     String newToken= refreshTokenService.refreshToken(refreshToken);
    //     RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(newToken,refreshToken);
    //     return ResponseEntity.ok(refreshTokenResponse);
    // }

    @GetMapping("/test")
    public String test() {
        log.info("Request come");
        return "Hello World";
    }
}

