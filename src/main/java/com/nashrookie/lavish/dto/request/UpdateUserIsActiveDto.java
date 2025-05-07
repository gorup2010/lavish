package com.nashrookie.lavish.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateUserIsActiveDto(@NotNull Boolean isActive) {

}
