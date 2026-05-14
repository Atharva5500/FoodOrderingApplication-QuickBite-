package com.quickbite.repository;

import com.quickbite.entity.Cart;
import com.quickbite.entity.CartItem;
import com.quickbite.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Find a specific item in a cart — used to update quantity
    Optional<CartItem> findByCartAndMenuItem(Cart cart, MenuItem menuItem);
}
