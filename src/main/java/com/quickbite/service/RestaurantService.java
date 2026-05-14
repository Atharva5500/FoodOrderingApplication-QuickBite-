package com.quickbite.service;

import com.quickbite.dto.request.RestaurantRequest;
import com.quickbite.dto.response.RestaurantResponse;
import com.quickbite.entity.Restaurant;

import java.util.List;

public interface RestaurantService {

    RestaurantResponse createRestaurant(RestaurantRequest request, String ownerEmail);

    RestaurantResponse updateRestaurant(Long restaurantId, RestaurantRequest request, String ownerEmail);

    RestaurantResponse toggleOpenStatus(Long restaurantId, String ownerEmail);

    void deleteRestaurant(Long restaurantId, String ownerEmail);

    List<RestaurantResponse> getAllOpenRestaurants();

    List<RestaurantResponse> searchRestaurants(String keyword);

    RestaurantResponse getRestaurantById(Long restaurantId);

    List<RestaurantResponse> getMyRestaurants(String ownerEmail);
}
