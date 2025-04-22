package com.nashrookie.lavish.dto.request;

import java.util.List;

import com.nashrookie.lavish.validation.OrderByConstraint;
import com.nashrookie.lavish.validation.SortByConstraint;

import jakarta.validation.constraints.AssertTrue;

public record ProductFilterDto(
        String name,
        Integer page,
        Integer size,
        Long minPrice,
        Long maxPrice,
        Boolean isFeatured,
        @OrderByConstraint String orderBy,
        @SortByConstraint String sortBy,
        List<Long> categorieIds) {

    @AssertTrue(message = "minPrice must be less than maxPrice")
    public boolean isMinPriceLessThanMaxPrice() {
        if (minPrice == null || maxPrice == null) {
            return true;
        }
        return minPrice < maxPrice;
    }
}
