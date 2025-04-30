package com.nashrookie.lavish.dto.filter;

import com.nashrookie.lavish.validation.SortOrderConstraint;
import com.nashrookie.lavish.validation.UserSortByConstraint;

public record UserFilterDto(
        String username,
        Integer page,
        Integer size,
        @UserSortByConstraint String sortBy,
        @SortOrderConstraint String sortOrder) {
}
