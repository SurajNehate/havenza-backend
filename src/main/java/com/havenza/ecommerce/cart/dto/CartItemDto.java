package com.havenza.ecommerce.cart.dto;

import com.havenza.ecommerce.cart.CartItemEntity;
import com.havenza.ecommerce.product.dto.VariantDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemDto {
    private Long id;
    private VariantDto variant;
    private Integer quantity;

    public static CartItemDto fromEntity(CartItemEntity entity) {
        return CartItemDto.builder()
                .id(entity.getId())
                .variant(VariantDto.fromEntity(entity.getVariant()))
                .quantity(entity.getQuantity())
                .build();
    }
}
