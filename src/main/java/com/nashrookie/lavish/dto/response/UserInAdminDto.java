package com.nashrookie.lavish.dto.response;

import java.time.ZonedDateTime;

import com.nashrookie.lavish.entity.User;

public record UserInAdminDto(
        Long id,
        String username,
        String firstname,
        String lastname,
        ZonedDateTime createdOn) {

    public static UserInAdminDto fromModel(User user) {
        return new UserInAdminDto(
                user.getId(),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getCreatedOn()
        );
    }
}