package com.havenza.ecommerce.banner;

import com.havenza.ecommerce.banner.dto.BannerDto;
import com.havenza.ecommerce.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BannerDto>>> getActiveBanners() {
        return ResponseEntity.ok(ApiResponse.success(bannerService.getActiveBanners()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BannerDto>> createBanner(@RequestBody BannerDto request) {
        return ResponseEntity.ok(ApiResponse.success(bannerService.createBanner(request), "Banner created"));
    }
}
