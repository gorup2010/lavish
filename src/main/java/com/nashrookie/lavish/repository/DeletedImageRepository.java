package com.nashrookie.lavish.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.nashrookie.lavish.entity.DeletedImage;

public interface DeletedImageRepository extends JpaRepository<DeletedImage, Long> {
    
}
