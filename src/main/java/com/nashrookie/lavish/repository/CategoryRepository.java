package com.nashrookie.lavish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nashrookie.lavish.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    <T> List<T> findAllBy(Class<T> type);
}
