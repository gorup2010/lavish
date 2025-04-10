package com.nashrookie.lavish.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login() {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/test")
    public String test() {
        log.info("Request come");
        return "Hello World";
    }
}

