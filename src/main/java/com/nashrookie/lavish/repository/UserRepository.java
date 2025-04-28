package com.nashrookie.lavish.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nashrookie.lavish.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIsActiveTrueAndUsername(String username);
    @EntityGraph(attributePaths = {"roles"}, type = EntityGraph.EntityGraphType.FETCH)
    Optional<User> findWithRolesByIsActiveTrueAndUsername(String username);
}