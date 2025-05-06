package com.nashrookie.lavish.repository;

import org.springframework.data.repository.CrudRepository;

import com.nashrookie.lavish.entity.BlockedUser;

public interface BlockedUserRepository extends CrudRepository<BlockedUser, String> {
    
}