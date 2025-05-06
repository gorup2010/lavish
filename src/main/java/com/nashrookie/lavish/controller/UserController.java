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
import com.nashrookie.lavish.entity.User;
import com.nashrookie.lavish.exception.ResourceNotFoundException;
import com.nashrookie.lavish.repository.UserRepository;
import com.nashrookie.lavish.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PatchMapping("/{id}/is-active")
    @Secured("ADMIN")
    public ResponseEntity<String> updateUserActiveStatus(@PathVariable Long id, @RequestBody UpdateUserIsActiveDto isActiveDto) {
        userService.updateUserActiveStatus(id, isActiveDto);
        return ResponseEntity.ok("Update user active status successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDto> getUser(@PathVariable Long id) {
        User user = userRepository.findWithRolesById(id).orElseThrow(() -> {
            log.error("Not found user with id {} in getUser", id);
            return new ResourceNotFoundException();
        });

        return ResponseEntity.ok(UserDetailsDto.fromModel(user));
    }
}
