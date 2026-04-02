package com.havenza.ecommerce.order;

import com.havenza.ecommerce.auth.UserEntity;
import com.havenza.ecommerce.auth.UserRepository;
import com.havenza.ecommerce.cart.CartEntity;
import com.havenza.ecommerce.cart.CartService;
import com.havenza.ecommerce.common.PagedResponse;
import com.havenza.ecommerce.common.exception.BusinessRuleException;
import com.havenza.ecommerce.common.exception.ResourceNotFoundException;
import com.havenza.ecommerce.common.exception.UnauthorizedException;
import com.havenza.ecommerce.coupon.CouponEntity;
import com.havenza.ecommerce.coupon.CouponRepository;
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
    private final CouponRepository couponRepository;

    @Transactional
    public OrderDto placeOrder(Long userId, PlaceOrderRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CartEntity cart = cartService.getOrCreateCart(userId);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessRuleException("Cart is empty");
        }

        BigDecimal totalAmount = cart.getItems().stream()
                .map(item -> item.getVariant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Process coupon discount if provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            CouponEntity coupon = couponRepository.findByCode(request.getCouponCode().toUpperCase())
                    .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

            if (!coupon.isActive()) {
                throw new BusinessRuleException("Coupon is inactive");
            }

            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidUntil())) {
                throw new BusinessRuleException("Coupon is expired or not yet valid");
            }

            if (coupon.getMinOrderAmount() != null && totalAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
                throw new BusinessRuleException("Order total does not meet the minimum amount for this coupon");
            }

            // Calculate discount: percentage of total, capped at maxDiscountAmount
            discountAmount = totalAmount.multiply(coupon.getDiscountPercentage()).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            if (coupon.getMaxDiscountAmount() != null && discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discountAmount = coupon.getMaxDiscountAmount();
            }
        }

        OrderEntity order = OrderEntity.builder()
                .user(user)
                .status(OrderStatus.PLACED)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .build();

        // Validate stock availability before creating order items
        for (var cartItem : cart.getItems()) {
            var variant = cartItem.getVariant();
            if (variant.getStockQuantity() != null && cartItem.getQuantity() > variant.getStockQuantity()) {
                throw new BusinessRuleException(
                    "Insufficient stock for '" + variant.getName() + "'. Available: " + variant.getStockQuantity());
            }
        }

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

        // Decrement stock quantities after successful order placement
        for (var cartItem : cart.getItems()) {
            var variant = cartItem.getVariant();
            if (variant.getStockQuantity() != null) {
                variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
            }
        }

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
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to view this order");
        }

        return OrderDto.fromEntity(order);
    }

    @Transactional
    public OrderDto cancelOrder(Long orderId, Long userId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to cancel this order");
        }

        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BusinessRuleException("Order cannot be cancelled at this stage");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return OrderDto.fromEntity(orderRepository.save(order));
    }
}
