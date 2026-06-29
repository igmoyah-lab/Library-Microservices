package com.library.users.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.users.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByAuthEmailIgnoreCase(String authEmail);

    boolean existsByAuthEmailIgnoreCase(String authEmail);
}
