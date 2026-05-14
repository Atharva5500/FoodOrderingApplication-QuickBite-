package com.quickbite.repository;

import com.quickbite.entity.Order;
import com.quickbite.entity.Restaurant;
import com.quickbite.entity.User;
import com.quickbite.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    // Get all orders for a customer — order history page
    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);

    // Get all orders for a restaurant — owner dashboard
    List<Order> findByRestaurantOrderByCreatedAtDesc(Restaurant restaurant);

    // Get active orders for a restaurant — filter by status
    List<Order> findByRestaurantAndStatus(Restaurant restaurant, OrderStatus status);

    // Get all orders for admin
    List<Order> findAllByOrderByCreatedAtDesc();

}
