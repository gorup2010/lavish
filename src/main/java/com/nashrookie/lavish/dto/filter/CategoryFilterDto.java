package com.nashrookie.lavish.dto.filter;

import com.nashrookie.lavish.validation.CategorySortByConstraint;
import com.nashrookie.lavish.validation.SortOrderConstraint;

public record CategoryFilterDto(
        String name,
        Integer page,
        Integer size,
        @CategorySortByConstraint String sortBy,
        @SortOrderConstraint String sortOrder) {
}
