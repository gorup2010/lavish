package com.nashrookie.lavish.dto.response;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class RatingDto {
    private Long id;
    private String comment;
    private Integer star;
    private ZonedDateTime createdOn;
    private OwnerDto user;

    public RatingDto(Long id, String comment, Integer star, ZonedDateTime createdOn, Long userId, String userFirstname, String userLastname) {
        this.id = id;
        this.comment = comment;
        this.star = star;
        this.createdOn = createdOn;
        this.user = new OwnerDto(userId, userFirstname, userLastname);
    }
}
