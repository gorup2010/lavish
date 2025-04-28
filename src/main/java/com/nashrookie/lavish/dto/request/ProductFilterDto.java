package com.nashrookie.lavish.dto.request;

import java.util.List;

import com.nashrookie.lavish.validation.SortByConstraint;
import com.nashrookie.lavish.validation.SortOrderConstraint;

import jakarta.validation.constraints.AssertTrue;

public record ProductFilterDto(
        String name,
        Integer page,
        Integer size,
        Long minPrice,
        Long maxPrice,
        Boolean isFeatured,
        @SortByConstraint String sortBy,
        @SortOrderConstraint String sortOrder,
        List<Long> categoryIds) {

    @AssertTrue(message = "minPrice must be less than maxPrice")
    public boolean isMinPriceLessThanMaxPrice() {
        if (minPrice == null || maxPrice == null) {
            return true;
        }
        return minPrice < maxPrice;
    }
}
