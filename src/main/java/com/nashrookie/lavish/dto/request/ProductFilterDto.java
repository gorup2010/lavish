package com.nashrookie.lavish.dto.request;

import java.util.List;


public record ProductFilterDto (
    String name,
    Integer page,
    Integer size,
    Long minPrice,
    Long maxPrice,
    Boolean isFeatured,
    String orderBy,
    String sortBy,
    List<Long> categorieIds
) {
}
