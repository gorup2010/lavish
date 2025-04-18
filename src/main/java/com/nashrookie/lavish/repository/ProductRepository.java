package com.nashrookie.lavish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nashrookie.lavish.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
