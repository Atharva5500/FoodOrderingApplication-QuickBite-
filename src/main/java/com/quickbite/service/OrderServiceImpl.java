package com.quickbite.service;

import com.quickbite.dto.request.PlaceOrderRequest;
import com.quickbite.dto.request.UpdateOrderStatusRequest;
import com.quickbite.dto.response.OrderItemResponse;
import com.quickbite.dto.response.OrderResponse;
import com.quickbite.entity.*;
import com.quickbite.enums.OrderStatus;
import com.quickbite.exception.BadRequestException;
import com.quickbite.exception.ResourceNotFoundException;
import com.quickbite.exception.UnauthorizedException;
import com.quickbite.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final RestaurantRepository restaurantRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request, String customerEmail) {

        User customer = getUserByEmail(customerEmail);

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new BadRequestException("Your cart is empty"));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty. Add items before placing an order.");
        }

        Restaurant restaurant = cart.getRestaurant();
        if (!restaurant.isOpen()) {
            throw new BadRequestException("This restaurant is now closed. Cannot place order.");
        }

        BigDecimal totalAmount = cart.getCartItems()
                .stream()
                .map(item -> item.getPriceAtAddition()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .customer(customer)
                .restaurant(restaurant)
                .totalAmount(totalAmount)
                .deliveryAddress(request.getDeliveryAddress())
                .build();

        List<OrderItem> orderItems = cart.getCartItems()
                .stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .menuItem(cartItem.getMenuItem())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getPriceAtAddition())
                        .build())
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);
        orderRepository.save(order);
        cartService.clearCart(customerEmail);

        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String customerEmail) {

        User customer = getUserByEmail(customerEmail);
        return orderRepository.findByCustomerOrderByCreatedAtDesc(customer)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, String userEmail) {

        User user = getUserByEmail(userEmail);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));

        // ✅ Only customer and owner check — no isAdmin check
        // Admin uses /api/admin/orders/{id} which goes through AdminService
        boolean isCustomer = order.getCustomer().getId().equals(user.getId());
        boolean isOwner    = order.getRestaurant().getOwner().getId().equals(user.getId());

        if (!isCustomer && !isOwner) {
            throw new UnauthorizedException("You are not allowed to view this order");
        }

        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId,
                                           UpdateOrderStatusRequest request,
                                           String ownerEmail) {

        User owner = getUserByEmail(ownerEmail);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));

        if (!order.getRestaurant().getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You are not allowed to update this order");
        }

        validateStatusTransition(order.getStatus(), request.getStatus());
        order.setStatus(request.getStatus());

        return mapToResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getRestaurantOrders(Long restaurantId, String ownerEmail) {

        User owner = getUserByEmail(ownerEmail);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with id: " + restaurantId));

        // ✅ Only owner check — no isAdmin check
        // Admin uses /api/admin/restaurants/{id}/orders through AdminService
        if (!restaurant.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You do not own this restaurant");
        }

        return orderRepository.findByRestaurantOrderByCreatedAtDesc(restaurant)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── Private Helper Methods ──────────────────────────────────

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {

        if (next == OrderStatus.CANCELLED) {
            if (current != OrderStatus.PLACED) {
                throw new BadRequestException(
                        "Order can only be cancelled when it is in PLACED status");
            }
            return;
        }

        List<OrderStatus> sequence = List.of(
                OrderStatus.PLACED,
                OrderStatus.CONFIRMED,
                OrderStatus.PREPARING,
                OrderStatus.OUT_FOR_DELIVERY,
                OrderStatus.DELIVERED
        );

        int currentIndex = sequence.indexOf(current);
        int nextIndex    = sequence.indexOf(next);

        // ✅ Guard against invalid status values
        if (currentIndex == -1) {
            throw new BadRequestException("Current order status is invalid: " + current);
        }
        if (nextIndex == -1) {
            throw new BadRequestException("Requested order status is invalid: " + next);
        }
        if (nextIndex != currentIndex + 1) {
            throw new BadRequestException(
                    "Invalid status transition from " + current + " to " + next);
        }
    }

    // ✅ mapToResponse is package-accessible so AdminServiceImpl can reuse it
    OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> itemResponses = order.getOrderItems()
                .stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .menuItemId(item.getMenuItem().getId())
                        .menuItemName(item.getMenuItem().getName())
                        .imageUrl(item.getMenuItem().getImageUrl())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .restaurantId(order.getRestaurant().getId())
                .restaurantName(order.getRestaurant().getName())
                .restaurantImage(order.getRestaurant().getImageUrl())
                .customerName(order.getCustomer().getName())
                .deliveryAddress(order.getDeliveryAddress())
                .items(itemResponses)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}