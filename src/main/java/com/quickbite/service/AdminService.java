package com.quickbite.service;

import com.quickbite.dto.response.AdminUserResponse;
import com.quickbite.dto.response.OrderResponse;
import com.quickbite.dto.response.RestaurantResponse;
import com.quickbite.entity.User;

import java.util.List;

public interface AdminService {

    // ─── User Management ─────────────────────────────────────────
    List<AdminUserResponse> getAllUsers();
    void deleteUser(Long userId);

    // ─── Restaurant Management ───────────────────────────────────
    List<RestaurantResponse> getAllRestaurants();
    void deleteRestaurant(Long restaurantId);

    // ─── Order Management ────────────────────────────────────────
    // ✅ Moved from OrderService — admin only, no ownership checks
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getAllRestaurantOrders(Long restaurantId);
}
