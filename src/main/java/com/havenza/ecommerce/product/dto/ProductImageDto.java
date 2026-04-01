package com.havenza.ecommerce.product.dto;

import com.havenza.ecommerce.product.ProductImageEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageDto {
    private Long id;
    private String imageUrl;
    private Integer sortOrder;

    public static ProductImageDto fromEntity(ProductImageEntity entity) {
        return ProductImageDto.builder()
                .id(entity.getId())
                .imageUrl(entity.getImageUrl())
                .sortOrder(entity.getSortOrder())
                .build();
    }
}
