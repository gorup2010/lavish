package com.nashrookie.lavish.repository;

import org.springframework.data.repository.CrudRepository;

import com.nashrookie.lavish.entity.Token;

public interface TokenRepository extends CrudRepository<Token, String> {
    
}
