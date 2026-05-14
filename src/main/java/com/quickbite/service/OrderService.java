package com.quickbite.service;

import com.quickbite.dto.request.PlaceOrderRequest;
import com.quickbite.dto.request.UpdateOrderStatusRequest;
import com.quickbite.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    // Customer places an order from their cart
    OrderResponse placeOrder(PlaceOrderRequest request, String customerEmail);

    // Customer views their order history
    List<OrderResponse> getMyOrders(String customerEmail);

    // Customer or Owner tracks a specific order
    OrderResponse getOrderById(Long orderId, String userEmail);

    // Restaurant Owner updates the order status
    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request, String ownerEmail);

    // Restaurant Owner views all orders for their restaurant
    List<OrderResponse> getRestaurantOrders(Long restaurantId, String ownerEmail);


}
