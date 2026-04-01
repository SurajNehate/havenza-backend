package com.havenza.ecommerce.review;

import com.havenza.ecommerce.auth.UserDetailsImpl;
import com.havenza.ecommerce.common.ApiResponse;
import com.havenza.ecommerce.common.PagedResponse;
import com.havenza.ecommerce.review.dto.CreateReviewRequest;
import com.havenza.ecommerce.review.dto.ReviewDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDto>> addReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.addReview(userDetails.getId(), request), "Review added successfully"));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDto>>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getProductReviews(productId, page, size)));
    }
    
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Review deleted"));
    }
}
