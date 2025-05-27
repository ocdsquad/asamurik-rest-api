package com.example.asamurik_rest_api.repository;

import com.example.asamurik_rest_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
