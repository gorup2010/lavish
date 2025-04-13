package com.nashrookie.lavish.dto.response;

import lombok.Builder;

@Builder
public record ErrorResponse(
        Integer code,
        String message) {
}
