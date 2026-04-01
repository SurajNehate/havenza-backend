package com.havenza.ecommerce.product.dto;

import com.havenza.ecommerce.product.ProductEntity;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal basePrice;
    private CategoryDto category;
    private String thumbnailUrl;
    private boolean active;
    private LocalDateTime createdAt;
    private List<VariantDto> variants;
    private List<ProductImageDto> images;

    public static ProductDto fromEntity(ProductEntity entity) {
        return ProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .basePrice(entity.getBasePrice())
                .category(CategoryDto.fromEntity(entity.getCategory()))
                .thumbnailUrl(entity.getThumbnailUrl())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .variants(entity.getVariants() != null ? 
                        entity.getVariants().stream().map(VariantDto::fromEntity).collect(Collectors.toList()) : null)
                .images(entity.getImages() != null ? 
                        entity.getImages().stream().map(ProductImageDto::fromEntity).collect(Collectors.toList()) : null)
                .build();
    }
}
