package com.havenza.ecommerce.product.dto;

import com.havenza.ecommerce.product.VariantEntity;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class VariantDto {
    private Long id;
    private String sku;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private Map<String, String> attributes;
    private String productName;
    private String productSlug;

    public static VariantDto fromEntity(VariantEntity entity) {
        return VariantDto.builder()
                .id(entity.getId())
                .sku(entity.getSku())
                .name(entity.getName())
                .price(entity.getPrice())
                .stockQuantity(entity.getStockQuantity())
                .imageUrl(entity.getImageUrl() != null && !entity.getImageUrl().trim().isEmpty() ? entity.getImageUrl() : (entity.getProduct() != null ? entity.getProduct().getThumbnailUrl() : null))
                .attributes(entity.getAttributes())
                .productName(entity.getProduct() != null ? entity.getProduct().getName() : null)
                .productSlug(entity.getProduct() != null ? entity.getProduct().getSlug() : null)
                .build();
    }
}
