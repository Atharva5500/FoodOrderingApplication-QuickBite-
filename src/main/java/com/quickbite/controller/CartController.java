package com.quickbite.controller;

import com.quickbite.dto.request.AddToCartRequest;
import com.quickbite.dto.response.ApiResponse;
import com.quickbite.dto.response.CartResponse;
import com.quickbite.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart(
            Authentication authentication) {

        String email = authentication.getName();
        CartResponse cart = cartService.getMyCart(email);
        return ResponseEntity.ok(ApiResponse.success("Cart fetched", cart));
    }


    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        CartResponse cart = cartService.addToCart(request, email);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", cart));
    }


    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(
            @PathVariable Long cartItemId,
            Authentication authentication) {

        String email = authentication.getName();
        CartResponse cart = cartService.removeFromCart(cartItemId, email);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cart));
    }


    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            Authentication authentication) {

        String email = authentication.getName();
        cartService.clearCart(email);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared"));
    }
}
