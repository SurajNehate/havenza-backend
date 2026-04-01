package com.havenza.ecommerce.wishlist.dto;

import com.havenza.ecommerce.product.dto.ProductDto;
import com.havenza.ecommerce.wishlist.WishlistEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WishlistDto {
    private Long id;
    private Long userId;
    private ProductDto product;
    private LocalDateTime createdAt;

    public static WishlistDto fromEntity(WishlistEntity entity) {
        return WishlistDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .product(ProductDto.fromEntity(entity.getProduct()))
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
