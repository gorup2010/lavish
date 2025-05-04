package com.nashrookie.lavish.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nashrookie.lavish.dto.request.UpdateUserIsActiveDto;
import com.nashrookie.lavish.service.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PatchMapping("/{id}/is-active")
    @Secured("ADMIN")
    public ResponseEntity<Void> updateUserActiveStatus(@PathVariable Long id, @RequestBody UpdateUserIsActiveDto isActiveDto) {
        userService.updateUserActiveStatus(id, isActiveDto);
        return ResponseEntity.ok().build();
    }
}
