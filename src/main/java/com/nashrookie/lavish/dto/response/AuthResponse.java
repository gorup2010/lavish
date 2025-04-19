package com.nashrookie.lavish.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record AuthResponse(Long id, String username, List<String> roles, String accessToken)
 {}
