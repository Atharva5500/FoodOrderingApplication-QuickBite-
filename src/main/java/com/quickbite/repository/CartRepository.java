package com.quickbite.repository;

import com.quickbite.entity.Cart;
import com.quickbite.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    // Get cart for a specific customer — each customer has only one cart
    Optional<Cart> findByCustomer(User customer);
}
