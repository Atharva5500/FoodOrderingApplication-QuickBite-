package com.quickbite.repository;

import com.quickbite.entity.MenuItem;
import com.quickbite.entity.Restaurant;
import com.quickbite.enums.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem,Long> {

    // Get all menu items for a restaurant
    List<MenuItem> findByRestaurant(Restaurant restaurant);

    // Get only available items for a restaurant — shown to customers
    List<MenuItem> findByRestaurantAndIsAvailableTrue(Restaurant restaurant);

    // Get items by category for a restaurant — for category filter
    List<MenuItem> findByRestaurantAndCategory(Restaurant restaurant, FoodCategory category);


}
