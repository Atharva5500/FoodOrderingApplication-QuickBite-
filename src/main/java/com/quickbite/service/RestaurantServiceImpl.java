package com.quickbite.service;

import com.quickbite.dto.request.RestaurantRequest;
import com.quickbite.dto.response.RestaurantResponse;
import com.quickbite.entity.Restaurant;
import com.quickbite.entity.User;
import com.quickbite.exception.ResourceNotFoundException;
import com.quickbite.exception.UnauthorizedException;
import com.quickbite.repository.RestaurantRepository;
import com.quickbite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RestaurantResponse createRestaurant(RestaurantRequest request, String ownerEmail) {

        User owner = getUserByEmail(ownerEmail);

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .description(request.getDescription())
                .cuisineType(request.getCuisineType())
                .address(request.getAddress())
                .imageUrl(request.getImageUrl())
                .isOpen(true)
                .rating(0.0)
                .owner(owner)
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public RestaurantResponse updateRestaurant(Long restaurantId,
                                               RestaurantRequest request,
                                               String ownerEmail) {

        Restaurant restaurant = getRestaurantOwnedBy(restaurantId, ownerEmail);

        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setAddress(request.getAddress());

        if (request.getImageUrl() != null) {
            restaurant.setImageUrl(request.getImageUrl());
        }

        Restaurant updated = restaurantRepository.save(restaurant);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public RestaurantResponse toggleOpenStatus(Long restaurantId, String ownerEmail) {

        Restaurant restaurant = getRestaurantOwnedBy(restaurantId, ownerEmail);
        restaurant.setOpen(!restaurant.isOpen());

        Restaurant updated = restaurantRepository.save(restaurant);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteRestaurant(Long restaurantId, String ownerEmail) {

        Restaurant restaurant = getRestaurantOwnedBy(restaurantId, ownerEmail);
        restaurantRepository.delete(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllOpenRestaurants() {

        return restaurantRepository.findByIsOpenTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> searchRestaurants(String keyword) {

        List<Restaurant> byName = restaurantRepository
                .findByNameContainingIgnoreCase(keyword);
        List<Restaurant> byCuisine = restaurantRepository
                .findByCuisineTypeContainingIgnoreCase(keyword);

        // ✅ Merge both lists, remove duplicates, filter open only
        return Stream.concat(byName.stream(), byCuisine.stream())
                .distinct()
                .filter(Restaurant::isOpen)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with id: " + restaurantId));
        return mapToResponse(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getMyRestaurants(String ownerEmail) {

        User owner = getUserByEmail(ownerEmail);
        return restaurantRepository.findByOwner(owner)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── Private Helper Methods ──────────────────────────────────

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Restaurant getRestaurantOwnedBy(Long restaurantId, String ownerEmail) {
        User owner = getUserByEmail(ownerEmail);
        return restaurantRepository.findByIdAndOwner(restaurantId, owner)
                .orElseThrow(() -> new UnauthorizedException(
                        "You do not own this restaurant or it does not exist"));
    }

    private RestaurantResponse mapToResponse(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .cuisineType(restaurant.getCuisineType())
                .address(restaurant.getAddress())
                .imageUrl(restaurant.getImageUrl())
                .isOpen(restaurant.isOpen())
                .rating(restaurant.getRating())
                .ownerName(restaurant.getOwner().getName())
                .build();
    }
}