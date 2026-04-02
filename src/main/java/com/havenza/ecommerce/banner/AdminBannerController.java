package com.havenza.ecommerce.banner;

import com.havenza.ecommerce.banner.dto.BannerDto;
import com.havenza.ecommerce.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/banners")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBannerController {

    private final BannerService bannerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BannerDto>>> getAllBanners() {
        return ResponseEntity.ok(ApiResponse.success(bannerService.getAllBanners()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BannerDto>> createBanner(@RequestBody BannerDto request) {
        return ResponseEntity.ok(ApiResponse.success(bannerService.createBanner(request), "Banner created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerDto>> updateBanner(@PathVariable Long id, @RequestBody BannerDto request) {
        return ResponseEntity.ok(ApiResponse.success(bannerService.updateBanner(id, request), "Banner updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Banner deleted"));
    }
}
