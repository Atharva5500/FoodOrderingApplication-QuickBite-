package com.quickbite.controller;

import com.quickbite.dto.response.AdminUserResponse;
import com.quickbite.dto.response.ApiResponse;
import com.quickbite.dto.response.OrderResponse;
import com.quickbite.dto.response.RestaurantResponse;
import com.quickbite.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ─── USER MANAGEMENT ─────────────────────────────────────────


    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers() {
        List<AdminUserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users", users));
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    // ─── RESTAURANT MANAGEMENT ───────────────────────────────────


    @GetMapping("/restaurants")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getAllRestaurants() {
        List<RestaurantResponse> restaurants = adminService.getAllRestaurants();
        return ResponseEntity.ok(ApiResponse.success("All restaurants", restaurants));
    }


    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable Long id) {
        adminService.deleteRestaurant(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT).build();

    }

    // ─── ORDER MANAGEMENT ────────────────────────────────────────



    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orders = adminService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success("All orders", orders));
    }


    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id) {
        OrderResponse order = adminService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success("Order fetched", order));
    }


    @GetMapping("/restaurants/{id}/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getRestaurantOrders(
            @PathVariable Long id) {
        List<OrderResponse> orders = adminService.getAllRestaurantOrders(id);
        return ResponseEntity.ok(ApiResponse.success("Restaurant orders", orders));
    }
}
