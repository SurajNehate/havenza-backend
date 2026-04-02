package com.havenza.ecommerce.coupon;

import com.havenza.ecommerce.coupon.dto.CouponDto;
import com.havenza.ecommerce.coupon.dto.CreateCouponRequest;
import com.havenza.ecommerce.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCouponController {

    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponDto>>> getAllCoupons() {
        return ResponseEntity.ok(ApiResponse.success(couponService.getAllCoupons()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CouponDto>> createCoupon(@RequestBody CreateCouponRequest request) {
        return ResponseEntity.ok(ApiResponse.success(couponService.createCoupon(request), "Coupon created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponDto>> updateCoupon(@PathVariable Long id, @RequestBody CreateCouponRequest request) {
        return ResponseEntity.ok(ApiResponse.success(couponService.updateCoupon(id, request), "Coupon updated"));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<CouponDto>> toggleCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(couponService.toggleCouponActive(id), "Coupon status toggled"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Coupon deleted"));
    }
}
