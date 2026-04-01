package com.havenza.ecommerce.review.dto;

import com.havenza.ecommerce.review.ReviewEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewDto {
    private Long id;
    private Long productId;
    private String userName;
    private Integer rating;
    private String comment;
    private boolean approved;
    private LocalDateTime createdAt;

    public static ReviewDto fromEntity(ReviewEntity entity) {
        return ReviewDto.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .userName(entity.getUser().getFullName())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .approved(entity.isApproved())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
