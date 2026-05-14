package com.quickbite.service;

import com.quickbite.dto.request.MenuItemRequest;
import com.quickbite.dto.response.MenuItemResponse;
import com.quickbite.entity.MenuItem;
import com.quickbite.entity.Restaurant;
import com.quickbite.entity.User;
import com.quickbite.enums.FoodCategory;
import com.quickbite.exception.ResourceNotFoundException;
import com.quickbite.exception.UnauthorizedException;
import com.quickbite.repository.MenuItemRepository;
import com.quickbite.repository.RestaurantRepository;
import com.quickbite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MenuItemResponse addMenuItem(Long restaurantId,
                                        MenuItemRequest request,
                                        String ownerEmail) {

        Restaurant restaurant = getRestaurantOwnedBy(restaurantId, ownerEmail);

        MenuItem menuItem = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .isAvailable(request.isAvailable())
                .restaurant(restaurant)
                .build();

        MenuItem saved = menuItemRepository.save(menuItem);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public MenuItemResponse updateMenuItem(Long menuItemId,
                                           MenuItemRequest request,
                                           String ownerEmail) {

        MenuItem menuItem = getMenuItemOwnedBy(menuItemId, ownerEmail);

        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setCategory(request.getCategory());
        menuItem.setAvailable(request.isAvailable());

        if (request.getImageUrl() != null) {
            menuItem.setImageUrl(request.getImageUrl());
        }

        MenuItem updated = menuItemRepository.save(menuItem);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public MenuItemResponse toggleAvailability(Long menuItemId, String ownerEmail) {

        MenuItem menuItem = getMenuItemOwnedBy(menuItemId, ownerEmail);
        menuItem.setAvailable(!menuItem.isAvailable());

        MenuItem updated = menuItemRepository.save(menuItem);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long menuItemId, String ownerEmail) {

        MenuItem menuItem = getMenuItemOwnedBy(menuItemId, ownerEmail);
        menuItemRepository.delete(menuItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuByRestaurant(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with id: " + restaurantId));

        return menuItemRepository.findByRestaurantAndIsAvailableTrue(restaurant)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuByCategory(Long restaurantId, FoodCategory category) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with id: " + restaurantId));

        return menuItemRepository.findByRestaurantAndCategory(restaurant, category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── Private Helper Methods ──────────────────────────────────

    private Restaurant getRestaurantOwnedBy(Long restaurantId, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return restaurantRepository.findByIdAndOwner(restaurantId, owner)
                .orElseThrow(() -> new UnauthorizedException(
                        "You do not own this restaurant"));
    }

    private MenuItem getMenuItemOwnedBy(Long menuItemId, String ownerEmail) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu item not found with id: " + menuItemId));


        String restaurantOwnerEmail = menuItem.getRestaurant().getOwner().getEmail();
        if (!restaurantOwnerEmail.equals(ownerEmail)) {
            throw new UnauthorizedException("You do not own this menu item");
        }

        return menuItem;
    }

    private MenuItemResponse mapToResponse(MenuItem menuItem) {
        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .category(menuItem.getCategory())
                .imageUrl(menuItem.getImageUrl())
                .isAvailable(menuItem.isAvailable())
                .restaurantId(menuItem.getRestaurant().getId())
                .build();
    }
}