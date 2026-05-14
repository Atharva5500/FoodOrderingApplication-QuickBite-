package com.quickbite.service;

import com.quickbite.dto.request.AddToCartRequest;
import com.quickbite.dto.response.CartItemResponse;
import com.quickbite.dto.response.CartResponse;
import com.quickbite.entity.*;
import com.quickbite.exception.BadRequestException;
import com.quickbite.exception.ResourceNotFoundException;
import com.quickbite.exception.UnauthorizedException;
import com.quickbite.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartResponse addToCart(AddToCartRequest request, String customerEmail) {

        User customer = getUserByEmail(customerEmail);

        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        // ✅ Get restaurant from DB relationship — never trust frontend
        Restaurant restaurant = menuItem.getRestaurant();

        if (!menuItem.isAvailable()) {
            throw new BadRequestException("This item is currently unavailable");
        }

        if (!restaurant.isOpen()) {
            throw new BadRequestException("This restaurant is currently closed");
        }

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .customer(customer)
                            .restaurant(restaurant)
                            .build();
                    // ✅ save() needed — NEW entity not yet managed by JPA
                    return cartRepository.save(newCart);
                });

        // Single restaurant rule
        if (cart.getRestaurant() != null &&
                !cart.getRestaurant().getId().equals(restaurant.getId())) {
            throw new BadRequestException(
                    "Your cart has items from another restaurant. " +
                            "Please clear your cart before ordering from a different restaurant.");
        }

        cart.setRestaurant(restaurant);

        Optional<CartItem> existingItem = cartItemRepository
                .findByCartAndMenuItem(cart, menuItem);

        if (existingItem.isPresent()) {
            // Managed entity — dirty checking auto-updates on commit
            // ✅ No save() needed
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .menuItem(menuItem)
                    .quantity(request.getQuantity())
                    .priceAtAddition(menuItem.getPrice())
                    .build();
            // ✅ Add to collection FIRST so buildCartResponse() sees it in memory
            // Then save to DB
            cart.getCartItems().add(newCartItem);
            cartItemRepository.save(newCartItem);
        }

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(Long cartItemId, String customerEmail) {

        User customer = getUserByEmail(customerEmail);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getCustomer().getId().equals(customer.getId())) {
            throw new UnauthorizedException("This item does not belong to your cart");
        }

        Cart cart = cartItem.getCart();

        // ✅ Remove from collection first — keeps memory and DB in sync
        // orphanRemoval = true handles DB deletion automatically
        // No explicit cartItemRepository.delete() needed
        cart.getCartItems().remove(cartItem);

        // ✅ isEmpty() now reliable — memory matches DB state
        if (cart.getCartItems().isEmpty()) {
            cart.setRestaurant(null);
        }

        // cart is managed — dirty checking handles the update
        // explicit save() kept here for clarity
        cartRepository.save(cart);

        return buildCartResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
    // ✅ readOnly keeps Hibernate session open
    // Prevents LazyInitializationException on cart.getCartItems()
    public CartResponse getMyCart(String customerEmail) {

        User customer = getUserByEmail(customerEmail);
        Cart cart = cartRepository.findByCustomer(customer).orElse(null);

        if (cart == null || cart.getCartItems().isEmpty()) {
            return CartResponse.builder()
                    .totalAmount(BigDecimal.ZERO)
                    .totalItems(0)
                    .items(new ArrayList<>())
                    .build();
        }

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart(String customerEmail) {

        // ✅ Default propagation = REQUIRED
        // Joins existing transaction from placeOrder()
        // If placeOrder() rolls back, clearCart() rolls back too
        // DO NOT change to REQUIRES_NEW
        User customer = getUserByEmail(customerEmail);
        cartRepository.findByCustomer(customer).ifPresent(cart -> {
            // ✅ Depends on orphanRemoval = true in Cart.java
            // clear() removes from collection + DB automatically
            cart.getCartItems().clear();
            cart.setRestaurant(null);
            cartRepository.save(cart);
        });
    }

    // ── Private Helper Methods ──────────────────────────────────

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CartResponse buildCartResponse(Cart cart) {

        // ✅ Single pass — calculate totalAmount + totalItems in one loop
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalItems = 0;
        List<CartItemResponse> itemResponses = new ArrayList<>();

        for (CartItem item : cart.getCartItems()) {
            BigDecimal subtotal = item.getPriceAtAddition()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
            totalItems += item.getQuantity();

            itemResponses.add(CartItemResponse.builder()
                    .id(item.getId())
                    .menuItemId(item.getMenuItem().getId())
                    .menuItemName(item.getMenuItem().getName())
                    .imageUrl(item.getMenuItem().getImageUrl())
                    .price(item.getPriceAtAddition())
                    .quantity(item.getQuantity())
                    .subtotal(subtotal)
                    .build());
        }

        return CartResponse.builder()
                .id(cart.getId())
                .restaurantId(cart.getRestaurant() != null ?
                        cart.getRestaurant().getId() : null)
                .restaurantName(cart.getRestaurant() != null ?
                        cart.getRestaurant().getName() : null)
                .items(itemResponses)
                .totalAmount(totalAmount)
                .totalItems(totalItems)
                .build();
    }
}