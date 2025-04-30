package com.nashrookie.lavish.specification;

import org.springframework.data.jpa.domain.Specification;

import com.nashrookie.lavish.entity.Category;

public class CategorySpecification {
    public static Specification<Category> hasName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String lowerCaseName = name.trim().toLowerCase();
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + lowerCaseName + "%");
    }

    private CategorySpecification() {
    }
}