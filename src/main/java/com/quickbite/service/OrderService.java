package com.quickbite.service;

import com.quickbite.dto.request.PlaceOrderRequest;
import com.quickbite.dto.request.UpdateOrderStatusRequest;
import com.quickbite.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest request, String customerEmail);


    List<OrderResponse> getMyOrders(String customerEmail);


    OrderResponse getOrderById(Long orderId, String userEmail);


    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request, String ownerEmail);

    List<OrderResponse> getRestaurantOrders(Long restaurantId, String ownerEmail);


}
