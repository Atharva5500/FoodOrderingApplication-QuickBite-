package com.quickbite.controller;

import com.quickbite.dto.request.RestaurantRequest;
import com.quickbite.dto.response.ApiResponse;
import com.quickbite.dto.response.RestaurantResponse;
import com.quickbite.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    // ─── PUBLIC ENDPOINTS (Customer) ─────────────────────────────


    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getAllOpenRestaurants() {
        List<RestaurantResponse> restaurants=restaurantService.getAllOpenRestaurants();

        return ResponseEntity.ok(ApiResponse.success("Restaurants fetched", restaurants));
    }


    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> search(
            @RequestParam String keyword) {

        List<RestaurantResponse> restaurants = restaurantService.searchRestaurants(keyword);
        return ResponseEntity.ok(ApiResponse.success("Search results", restaurants));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponse>> getById(
            @PathVariable Long id) {

        RestaurantResponse restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(ApiResponse.success("Restaurant fetched", restaurant));
    }

    // ─── OWNER ENDPOINTS ─────────────────────────────────────────

    // GET /api/restaurants/my
    // Owner views their own restaurants
    // Authentication object is injected by Spring Security
    // authentication.getName() returns the logged-in user's email (from JWT)
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getMyRestaurants(
            Authentication authentication) {

        String email = authentication.getName();
        List<RestaurantResponse> restaurants = restaurantService.getMyRestaurants(email);
        return ResponseEntity.ok(ApiResponse.success("Your restaurants", restaurants));
    }

    // Owner creates a new restaurant
    @PostMapping
    public ResponseEntity<ApiResponse<RestaurantResponse>> create(
            @Valid @RequestBody RestaurantRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        RestaurantResponse restaurant = restaurantService.createRestaurant(request, email);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Restaurant created", restaurant));
    }


    // Owner updates their restaurant
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        RestaurantResponse restaurant = restaurantService.updateRestaurant(id, request, email);
        return ResponseEntity.ok(ApiResponse.success("Restaurant updated", restaurant));
    }

    // Owner toggles open/closed status
    // PATCH is used for partial updates — toggling one field
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<RestaurantResponse>> toggleStatus(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        RestaurantResponse restaurant = restaurantService.toggleOpenStatus(id, email);
        return ResponseEntity.ok(ApiResponse.success("Status updated", restaurant));
    }

    // Owner deletes their restaurant
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        restaurantService.deleteRestaurant(id, email);

        // 204 No Content — successful delete with no response body
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
