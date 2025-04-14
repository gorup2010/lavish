package com.nashrookie.lavish.dto.response;

import com.nashrookie.lavish.constant.Role;

import lombok.Builder;

@Builder
public record AuthResponse(Long id, String username, Role role, String accessToken)
 {}
