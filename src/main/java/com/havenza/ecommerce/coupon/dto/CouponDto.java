package com.havenza.ecommerce.coupon.dto;

import com.havenza.ecommerce.coupon.CouponEntity;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CouponDto {
    private Long id;
    private String code;
    private BigDecimal discountPercentage;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean active;

    public static CouponDto fromEntity(CouponEntity entity) {
        return CouponDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .discountPercentage(entity.getDiscountPercentage())
                .maxDiscountAmount(entity.getMaxDiscountAmount())
                .minOrderAmount(entity.getMinOrderAmount())
                .validFrom(entity.getValidFrom())
                .validUntil(entity.getValidUntil())
                .active(entity.isActive())
                .build();
    }
}
