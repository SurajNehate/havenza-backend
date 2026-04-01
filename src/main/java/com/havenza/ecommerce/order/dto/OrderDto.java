package com.havenza.ecommerce.order.dto;

import com.havenza.ecommerce.order.OrderEntity;
import com.havenza.ecommerce.order.OrderStatus;
import com.havenza.ecommerce.payment.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private String shippingAddress;
    private PaymentMethod paymentMethod;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderDto fromEntity(OrderEntity entity) {
        return OrderDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getFullName())
                .userEmail(entity.getUser().getEmail())
                .userPhone(entity.getUser().getPhone())
                .status(entity.getStatus())
                .totalAmount(entity.getTotalAmount())
                .discountAmount(entity.getDiscountAmount())
                .shippingAddress(entity.getShippingAddress())
                .paymentMethod(entity.getPaymentMethod())
                .items(entity.getItems().stream().map(OrderItemDto::fromEntity).collect(Collectors.toList()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
