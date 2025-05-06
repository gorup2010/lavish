package com.nashrookie.lavish.dto.response;

import java.util.List;

import com.nashrookie.lavish.entity.Role;
import com.nashrookie.lavish.entity.User;

public record UserDetailsDto(
        Long id,
        String username,
        String firstname,
        String lastname,
        List<String> roles,
        Boolean isActive) {
    public static UserDetailsDto fromModel(User user) {
        return new UserDetailsDto(user.getId(), user.getUsername(), user.getFirstname(), user.getLastname(),
                user.getRoles().stream().map(Role::getName).toList(), user.getIsActive());
    }
}
