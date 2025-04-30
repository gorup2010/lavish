package com.nashrookie.lavish.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nashrookie.lavish.dto.filter.CategoryFilterDto;
import com.nashrookie.lavish.dto.response.CategoryInAdminDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.entity.Category;
import com.nashrookie.lavish.repository.CategoryRepository;
import com.nashrookie.lavish.specification.CategorySpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public PaginationResponse<CategoryInAdminDto> getCategoriesAdminView(CategoryFilterDto categoryFilter,
            Pageable pageable) {
        Page<Category> products = this.getCategories(categoryFilter, pageable);

        return PaginationResponse.<CategoryInAdminDto>builder()
                .page(pageable.getPageNumber())
                .total(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .data(products.getContent().stream().map(CategoryInAdminDto::fromModel).toList())
                .build();
    }

    public Page<Category> getCategories(CategoryFilterDto categoryFilter, Pageable pageable) {
        return categoryRepository.findAll(Specification.where(CategorySpecification.hasName(categoryFilter.name())),
                pageable);
    }
}
