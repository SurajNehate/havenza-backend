package com.havenza.ecommerce.order;

import com.havenza.ecommerce.auth.UserEntity;
import com.havenza.ecommerce.auth.UserRepository;
import com.havenza.ecommerce.cart.CartEntity;
import com.havenza.ecommerce.cart.CartService;
import com.havenza.ecommerce.common.PagedResponse;
import com.havenza.ecommerce.order.dto.OrderDto;
import com.havenza.ecommerce.order.dto.PlaceOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Transactional
    public OrderDto placeOrder(Long userId, PlaceOrderRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartEntity cart = cartService.getOrCreateCart(userId);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        BigDecimal totalAmount = cart.getItems().stream()
                .map(item -> item.getVariant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // TODO: Handle Coupon processing here

        OrderEntity order = OrderEntity.builder()
                .user(user)
                .status(OrderStatus.PLACED)
                .totalAmount(totalAmount)
                .discountAmount(BigDecimal.ZERO) // Default, update with coupon
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .build();

        List<OrderItemEntity> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItemEntity.builder()
                        .order(order)
                        .variant(cartItem.getVariant())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getVariant().getPrice())
                        .build())
                .collect(Collectors.toList());

        order.setItems(orderItems);
        OrderEntity savedOrder = orderRepository.save(order);

        // Clear cart after successful order placement
        cartService.clearCart(userId);

        return OrderDto.fromEntity(savedOrder);
    }

    @Transactional(readOnly = true)
    public PagedResponse<OrderDto> getUserOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderEntity> orders = orderRepository.findByUserId(userId, pageable);
        return PagedResponse.of(orders.map(OrderDto::fromEntity));
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long orderId, Long userId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to view this order");
        }

        return OrderDto.fromEntity(order);
    }

    @Transactional
    public OrderDto cancelOrder(Long orderId, Long userId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to cancel this order");
        }

        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Order cannot be cancelled at this stage");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return OrderDto.fromEntity(orderRepository.save(order));
    }
}
