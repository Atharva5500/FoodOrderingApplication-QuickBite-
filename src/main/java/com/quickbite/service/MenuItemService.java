package com.quickbite.service;

import com.quickbite.dto.request.MenuItemRequest;
import com.quickbite.dto.response.MenuItemResponse;
import com.quickbite.enums.FoodCategory;

import java.util.List;

public interface MenuItemService {

    MenuItemResponse addMenuItem(Long restaurantId, MenuItemRequest menuItemRequest, String ownerEmail);

    MenuItemResponse updateMenuItem(Long menuItemId, MenuItemRequest menuItemRequest, String ownerEmail);

   MenuItemResponse toggleAvailability(Long menuItemId, String ownerEmail);

   void deleteMenuItem(Long menuItemId, String ownerEmail);

    List<MenuItemResponse> getMenuByRestaurant(Long restaurantId);

    List<MenuItemResponse> getMenuByCategory(Long restaurantId, FoodCategory category);
}
