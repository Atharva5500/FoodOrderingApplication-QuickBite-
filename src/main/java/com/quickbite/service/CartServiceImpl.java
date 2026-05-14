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

                    return cartRepository.save(newCart);
                });


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

            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .menuItem(menuItem)
                    .quantity(request.getQuantity())
                    .priceAtAddition(menuItem.getPrice())
                    .build();

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


        cart.getCartItems().remove(cartItem);


        if (cart.getCartItems().isEmpty()) {
            cart.setRestaurant(null);
        }


        cartRepository.save(cart);

        return buildCartResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
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


        User customer = getUserByEmail(customerEmail);
        cartRepository.findByCustomer(customer).ifPresent(cart -> {
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