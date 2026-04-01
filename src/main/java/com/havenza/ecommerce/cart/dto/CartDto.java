package com.havenza.ecommerce.cart.dto;

import com.havenza.ecommerce.cart.CartEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class CartDto {
    private Long id;
    private Long userId;
    private List<CartItemDto> items;
    private LocalDateTime updatedAt;

    public static CartDto fromEntity(CartEntity entity) {
        return CartDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .items(entity.getItems().stream().map(CartItemDto::fromEntity).collect(Collectors.toList()))
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
