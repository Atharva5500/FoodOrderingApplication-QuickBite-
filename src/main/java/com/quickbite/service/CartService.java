package com.quickbite.service;

import com.quickbite.dto.request.AddToCartRequest;
import com.quickbite.dto.response.CartResponse;

public interface CartService {

    CartResponse addToCart(AddToCartRequest request, String customerEmail);


    CartResponse removeFromCart(Long cartItemId, String customerEmail);


    CartResponse getMyCart(String customerEmail);


    void clearCart(String customerEmail);
}
