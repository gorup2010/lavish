package com.nashrookie.lavish.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(Long id, String username, String accessToken)
 {}
