package com.quickbite.service;

import com.quickbite.dto.request.AddToCartRequest;
import com.quickbite.dto.response.CartResponse;

public interface CartService {
    // Add an item to cart (or increase quantity if already in cart)
    CartResponse addToCart(AddToCartRequest request, String customerEmail);

    // Remove one item from cart
    CartResponse removeFromCart(Long cartItemId, String customerEmail);

    // Get current cart for logged-in customer
    CartResponse getMyCart(String customerEmail);

    // Clear all items from cart (called after order is placed)
    void clearCart(String customerEmail);
}
