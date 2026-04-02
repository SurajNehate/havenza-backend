package com.havenza.ecommerce.banner.dto;

import com.havenza.ecommerce.banner.BannerEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BannerDto {
    private Long id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private boolean active;
    private Integer sortOrder;
    private String termsAndConditions;

    public static BannerDto fromEntity(BannerEntity entity) {
        return BannerDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .imageUrl(entity.getImageUrl())
                .linkUrl(entity.getLinkUrl())
                .active(entity.isActive())
                .sortOrder(entity.getSortOrder())
                .termsAndConditions(entity.getTermsAndConditions())
                .build();
    }
}
