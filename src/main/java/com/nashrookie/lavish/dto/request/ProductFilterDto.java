package com.nashrookie.lavish.dto.request;

import java.util.List;

public record ProductFilterDto (
    String name,
    Long minPrice,
    Long maxPrice,
    Double minRate,
    Double maxRate,
    Boolean isFeatured,
    String orderBy,
    String sortBy,
    List<String> categories
) {
}
