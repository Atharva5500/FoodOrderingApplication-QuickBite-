package com.quickbite.repository;

import com.quickbite.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    // Find user by email — used during login & JWT validation
    Optional<User> findByEmail(String email);

    // Check if email already registered — used during registration
    boolean existsByEmail(String email);
}
