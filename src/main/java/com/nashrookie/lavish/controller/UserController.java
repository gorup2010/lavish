package com.nashrookie.lavish.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nashrookie.lavish.dto.filter.UserFilterDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.dto.response.UserInAdminDto;
import com.nashrookie.lavish.service.UserService;
import com.nashrookie.lavish.util.PaginationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/admin")
    public ResponseEntity<PaginationResponse<UserInAdminDto>> getUsersAdminView(
            @Valid @ModelAttribute UserFilterDto userFilter) {
        Pageable pageable = PaginationUtils.createPageable(userFilter.page(),
                userFilter.size(),
                userFilter.sortBy(),
                userFilter.sortOrder());

        return ResponseEntity.ok(userService.getUsersAdminView(userFilter, pageable));
    }

    @PatchMapping("/{id}/is-active")
    public ResponseEntity<Void> toggleInActive(@PathVariable Long id) {
        userService.toggleInActive(id);
        return ResponseEntity.ok().build();
    }

}
