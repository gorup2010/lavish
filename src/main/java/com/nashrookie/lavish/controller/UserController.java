package com.nashrookie.lavish.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nashrookie.lavish.dto.request.UpdateUserIsActiveDto;
import com.nashrookie.lavish.dto.response.UserDetailsDto;
import com.nashrookie.lavish.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @PatchMapping("/{id}/is-active")
    @Secured("ADMIN")
    public ResponseEntity<String> updateUserActiveStatus(@PathVariable Long id, @Valid @RequestBody UpdateUserIsActiveDto isActiveDto) {
        userService.updateUserActiveStatus(id, isActiveDto);
        return ResponseEntity.ok("Update user active status successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(UserDetailsDto.fromModel(userService.getUserDetails(id)));
    }
}
