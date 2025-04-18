package com.nashrookie.lavish.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nashrookie.lavish.entity.Role;
import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
