package com.nashrookie.lavish.specification;

import org.springframework.data.jpa.domain.Specification;

import com.nashrookie.lavish.entity.User;

public class UserSpecification {
    public static Specification<User> hasName(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        String lowerCaseName = username.trim().toLowerCase();
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")),
                "%" + lowerCaseName + "%");
    }

    private UserSpecification() {
    }
}
