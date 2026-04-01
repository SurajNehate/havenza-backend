package com.havenza.ecommerce.order.dto;

import com.havenza.ecommerce.order.OrderItemEntity;
import com.havenza.ecommerce.product.dto.VariantDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDto {
    private Long id;
    private VariantDto variant;
    private Integer quantity;
    private BigDecimal unitPrice;

    public static OrderItemDto fromEntity(OrderItemEntity entity) {
        return OrderItemDto.builder()
                .id(entity.getId())
                .variant(VariantDto.fromEntity(entity.getVariant()))
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .build();
    }
}
