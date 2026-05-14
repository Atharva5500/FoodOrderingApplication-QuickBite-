package com.quickbite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private String imageUrl;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subtotal; // price × quantity
}
