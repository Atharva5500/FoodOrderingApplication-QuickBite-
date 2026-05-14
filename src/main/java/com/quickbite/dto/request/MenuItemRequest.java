package com.quickbite.dto.request;

import com.quickbite.enums.FoodCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemRequest {

    @NotBlank(message = "Item name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format invalid")
    private BigDecimal price;

    @NotNull(message = "Category is required")
    private FoodCategory category;

    private String imageUrl;

    private boolean isAvailable = true;
}
