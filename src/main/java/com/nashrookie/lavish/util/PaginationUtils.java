package com.nashrookie.lavish.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.nashrookie.lavish.dto.response.PaginationResponse;

import java.util.function.Function;

public class PaginationUtils {

    public static <E, D> PaginationResponse<D> createPaginationResponse(
            Page<E> pageData,
            Function<E, D> entityToDtoMapper
    ) {
        return PaginationResponse.<D>builder()
                .page(pageData.getNumber())
                .total(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream().map(entityToDtoMapper).toList())
                .build();
    }

    public static Pageable createPageable(Integer page, Integer size, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }

    private PaginationUtils() {}
}
