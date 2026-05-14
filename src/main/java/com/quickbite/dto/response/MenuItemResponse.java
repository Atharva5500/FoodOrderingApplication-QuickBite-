package com.quickbite.dto.response;

import com.quickbite.enums.FoodCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private FoodCategory category;
    private String imageUrl;
    private boolean isAvailable;
    private Long restaurantId;
}
