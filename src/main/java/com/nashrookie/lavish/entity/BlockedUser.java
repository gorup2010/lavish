package com.nashrookie.lavish.entity;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;

@RedisHash(value = "blocked_users")
@AllArgsConstructor
public class BlockedUser {
    @Id
    private String id;

    @TimeToLive
    private Long expiration;
}