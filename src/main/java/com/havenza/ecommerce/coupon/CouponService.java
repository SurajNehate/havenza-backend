package com.havenza.ecommerce.coupon;

import com.havenza.ecommerce.common.exception.BusinessRuleException;
import com.havenza.ecommerce.common.exception.DuplicateResourceException;
import com.havenza.ecommerce.common.exception.ResourceNotFoundException;
import com.havenza.ecommerce.coupon.dto.CouponDto;
import com.havenza.ecommerce.coupon.dto.CreateCouponRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public java.util.List<CouponDto> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(CouponDto::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public CouponDto createCoupon(CreateCouponRequest request) {
        if (couponRepository.findByCode(request.getCode().toUpperCase()).isPresent()) {
            throw new DuplicateResourceException("Coupon code already exists");
        }

        CouponEntity coupon = CouponEntity.builder()
                .code(request.getCode().toUpperCase())
                .discountPercentage(request.getDiscountPercentage())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minOrderAmount(request.getMinOrderAmount())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .active(true)
                .build();

        return CouponDto.fromEntity(couponRepository.save(coupon));
    }

    @Transactional(readOnly = true)
    public CouponDto validateCoupon(String code) {
        CouponEntity coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        if (!coupon.isActive()) {
            throw new BusinessRuleException("Coupon is inactive");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidUntil())) {
            throw new BusinessRuleException("Coupon is expired or not yet valid");
        }

        return CouponDto.fromEntity(coupon);
    }
}
