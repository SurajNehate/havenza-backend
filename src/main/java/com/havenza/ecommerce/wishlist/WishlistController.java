package com.havenza.ecommerce.wishlist;

import com.havenza.ecommerce.auth.UserDetailsImpl;
import com.havenza.ecommerce.common.ApiResponse;
import com.havenza.ecommerce.common.PagedResponse;
import com.havenza.ecommerce.wishlist.dto.WishlistDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<WishlistDto>>> getWishlist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getUserWishlist(userDetails.getId(), page, size)));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<WishlistDto>> addToWishlist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.addToWishlist(userDetails.getId(), productId), "Added to wishlist"));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long productId) {
        wishlistService.removeFromWishlist(userDetails.getId(), productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Removed from wishlist"));
    }
}
