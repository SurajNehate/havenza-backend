package com.havenza.ecommerce.product.dto;

import com.havenza.ecommerce.product.CategoryEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private Long parentId;

    public static CategoryDto fromEntity(CategoryEntity entity) {
        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .build();
    }
}
