package com.quickbite.controller;

import com.quickbite.dto.request.MenuItemRequest;
import com.quickbite.dto.response.ApiResponse;
import com.quickbite.dto.response.MenuItemResponse;
import com.quickbite.enums.FoodCategory;
import com.quickbite.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    // ─── PUBLIC ENDPOINTS ─────────────────────────────────

    @GetMapping("/api/restaurants/{restaurantId}/menu")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getMenu(
            @PathVariable Long restaurantId) {
        List<MenuItemResponse> items = menuItemService.getMenuByRestaurant(restaurantId);
        return ResponseEntity.ok(ApiResponse.success("Menu fetched", items));
    }

    @GetMapping("/api/restaurants/{restaurantId}/menu/category")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getMenuByCategory(
            @PathVariable Long restaurantId,
            @RequestParam FoodCategory category) {
        List<MenuItemResponse> items = menuItemService.getMenuByCategory(restaurantId, category);
        return ResponseEntity.ok(ApiResponse.success("Menu fetched by category", items));
    }

    // ─── OWNER ENDPOINTS ──────────────────────────────────

    // POST still needs restaurantId — tells service which restaurant
    @PostMapping("/api/restaurants/{restaurantId}/menu")
    public ResponseEntity<ApiResponse<MenuItemResponse>> addItem(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        MenuItemResponse item = menuItemService.addMenuItem(restaurantId, request, email);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Menu item added", item));
    }


    @PutMapping("/api/menu/{menuItemId}")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateItem(
            @PathVariable Long menuItemId,
            @Valid @RequestBody MenuItemRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        MenuItemResponse item = menuItemService.updateMenuItem(menuItemId, request, email);
        return ResponseEntity.ok(ApiResponse.success("Menu item updated", item));
    }


    @PatchMapping("/api/menu/{menuItemId}/toggle")
    public ResponseEntity<ApiResponse<MenuItemResponse>> toggleItem(
            @PathVariable Long menuItemId,
            Authentication authentication) {
        String email = authentication.getName();
        MenuItemResponse item = menuItemService.toggleAvailability(menuItemId, email);
        return ResponseEntity.ok(ApiResponse.success("Item availability updated", item));
    }


    @DeleteMapping("/api/menu/{menuItemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long menuItemId,
            Authentication authentication) {
        String email = authentication.getName();
        menuItemService.deleteMenuItem(menuItemId, email);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
