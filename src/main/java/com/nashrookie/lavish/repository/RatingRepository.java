package com.nashrookie.lavish.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nashrookie.lavish.entity.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    <T> Page<T> findAllByProductId(Long id, Class<T> type, Pageable pageable);
}
