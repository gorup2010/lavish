package com.nashrookie.lavish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nashrookie.lavish.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
}
