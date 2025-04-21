package com.nashrookie.lavish.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.nashrookie.lavish.entity.Category;
import com.nashrookie.lavish.entity.Product;

public class ProductSpecification {
    public static Specification<Product> hasName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String lowerCaseName = name.trim().toLowerCase();
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + lowerCaseName + "%");
    }

    public static Specification<Product> hasCategory(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> root.join("categories").in(categories);
    }

    public static Specification<Product> hasIsFeatured(Boolean isFeatured) {
        if (isFeatured == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isFeatured"), isFeatured);
    }

    public static Specification<Product> hasPrice(Long minPrice, Long maxPrice) {
        String price = "price";
        if (minPrice == null && maxPrice == null) {
            return null;
        }
        if (minPrice == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(price), maxPrice);
        }
        if (maxPrice == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(price), minPrice);
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(price), minPrice, maxPrice);
    }

    private ProductSpecification() {
    }
}