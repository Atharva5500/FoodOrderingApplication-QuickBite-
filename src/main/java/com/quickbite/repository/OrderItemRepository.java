package com.quickbite.repository;

import com.quickbite.entity.Order;
import com.quickbite.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    // Get all items for a specific order
    List<OrderItem> findByOrder(Order order);
}
