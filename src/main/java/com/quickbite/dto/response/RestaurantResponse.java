package com.quickbite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {

    private Long id;
    private String name;
    private String description;
    private String cuisineType;
    private String address;
    private String imageUrl;
    private boolean isOpen;
    private Double rating;
    private String ownerName; // just the name, not full User object
}
