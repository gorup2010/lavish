package com.nashrookie.lavish.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nashrookie.lavish.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @EntityGraph(attributePaths = {"images", "categories"}, type = EntityGraph.EntityGraphType.FETCH)
    Optional<Product> findWithImagesAndCategoriesById(Long id);
}
