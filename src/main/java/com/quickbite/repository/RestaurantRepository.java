package com.quickbite.repository;

import com.quickbite.entity.Restaurant;
import com.quickbite.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {

    // Get all open restaurants — shown on home page
    List<Restaurant> findByIsOpenTrue();

    // Search restaurants by name (case insensitive) — for search bar
    List<Restaurant> findByNameContainingIgnoreCase(String name);

    // Search by cuisine type — for filter feature
    List<Restaurant> findByCuisineTypeContainingIgnoreCase(String cuisineType);

    // Get restaurants owned by a specific owner
    List<Restaurant> findByOwner(User owner);

    // Get a specific restaurant by id owned by a specific owner
    Optional<Restaurant> findByIdAndOwner(Long id, User owner);

}
