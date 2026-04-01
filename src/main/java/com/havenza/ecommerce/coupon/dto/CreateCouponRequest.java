package com.havenza.ecommerce.coupon.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateCouponRequest {
    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "100.0")
    private BigDecimal discountPercentage;

    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderAmount;

    @NotNull(message = "Valid from date is required")
    private LocalDateTime validFrom;

    @NotNull(message = "Valid until date is required")
    private LocalDateTime validUntil;
}
