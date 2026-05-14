package com.quickbite.controller;

import com.quickbite.dto.request.PlaceOrderRequest;
import com.quickbite.dto.request.UpdateOrderStatusRequest;
import com.quickbite.dto.response.ApiResponse;
import com.quickbite.dto.response.OrderResponse;
import com.quickbite.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ─── CUSTOMER ENDPOINTS ───────────────────────────────────────

    // POST /api/orders/place
    @PostMapping("/place")
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        OrderResponse order = orderService.placeOrder(request, email);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully!", order));
    }

    // GET /api/orders/my
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            Authentication authentication) {

        String email = authentication.getName();
        List<OrderResponse> orders = orderService.getMyOrders(email);
        return ResponseEntity.ok(ApiResponse.success("Your orders", orders));
    }

    // GET /api/orders/{id}
    // Customer or Owner only — no admin
    // ✅ Admin uses GET /api/admin/orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        OrderResponse order = orderService.getOrderById(id, email);
        return ResponseEntity.ok(ApiResponse.success("Order fetched", order));
    }

    // ─── OWNER ENDPOINTS ─────────────────────────────────────────

    // GET /api/orders/restaurant/{restaurantId}
    // Owner only — no admin
    // ✅ Admin uses GET /api/admin/restaurants/{id}/orders
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getRestaurantOrders(
            @PathVariable Long restaurantId,
            Authentication authentication) {

        String email = authentication.getName();
        List<OrderResponse> orders = orderService.getRestaurantOrders(restaurantId, email);
        return ResponseEntity.ok(ApiResponse.success("Restaurant orders", orders));
    }

    // PATCH /api/orders/{id}/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        OrderResponse order = orderService.updateOrderStatus(id, request, email);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", order));
    }

    // ✅ GET /api/orders/all REMOVED
    // Moved to GET /api/admin/orders in AdminController
}
