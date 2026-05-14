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


    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getMyRestaurants(
            Authentication authentication) {

        String email = authentication.getName();
        List<RestaurantResponse> restaurants = restaurantService.getMyRestaurants(email);
        return ResponseEntity.ok(ApiResponse.success("Your restaurants", restaurants));
    }


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



    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        RestaurantResponse restaurant = restaurantService.updateRestaurant(id, request, email);
        return ResponseEntity.ok(ApiResponse.success("Restaurant updated", restaurant));
    }


    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<RestaurantResponse>> toggleStatus(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        RestaurantResponse restaurant = restaurantService.toggleOpenStatus(id, email);
        return ResponseEntity.ok(ApiResponse.success("Status updated", restaurant));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        restaurantService.deleteRestaurant(id, email);


        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
