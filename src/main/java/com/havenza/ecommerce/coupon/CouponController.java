package com.havenza.ecommerce.coupon;

import com.havenza.ecommerce.common.ApiResponse;
import com.havenza.ecommerce.coupon.dto.CouponDto;
import com.havenza.ecommerce.coupon.dto.CreateCouponRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponDto>> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        return ResponseEntity.ok(ApiResponse.success(couponService.createCoupon(request), "Coupon created successfully"));
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<ApiResponse<CouponDto>> validateCoupon(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(couponService.validateCoupon(code), "Coupon is valid"));
    }
}
