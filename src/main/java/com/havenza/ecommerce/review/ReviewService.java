package com.havenza.ecommerce.review;

import com.havenza.ecommerce.auth.UserEntity;
import com.havenza.ecommerce.auth.UserRepository;
import com.havenza.ecommerce.common.PagedResponse;
import com.havenza.ecommerce.common.exception.BusinessRuleException;
import com.havenza.ecommerce.common.exception.DuplicateResourceException;
import com.havenza.ecommerce.common.exception.ResourceNotFoundException;
import com.havenza.ecommerce.common.exception.UnauthorizedException;
import com.havenza.ecommerce.order.OrderRepository;
import com.havenza.ecommerce.order.OrderStatus;
import com.havenza.ecommerce.product.ProductEntity;
import com.havenza.ecommerce.product.ProductRepository;
import com.havenza.ecommerce.review.dto.CreateReviewRequest;
import com.havenza.ecommerce.review.dto.ReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ReviewDto addReview(Long userId, CreateReviewRequest request) {
        if (reviewRepository.existsByUserIdAndProductId(userId, request.getProductId())) {
            throw new DuplicateResourceException("You have already reviewed this product");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Verify user has purchased the product before allowing review
        if (!orderRepository.existsByUserIdAndItems_Variant_Product_IdAndStatusIn(
                userId, request.getProductId(),
                java.util.List.of(OrderStatus.DELIVERED, OrderStatus.CONFIRMED, OrderStatus.SHIPPED))) {
            throw new BusinessRuleException("You can only review products you have purchased");
        }

        ReviewEntity review = ReviewEntity.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .approved(true) // Set to false if approval workflow is needed
                .build();

        return ReviewDto.fromEntity(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public PagedResponse<ReviewDto> getProductReviews(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewEntity> reviews = reviewRepository.findByProductIdAndApprovedTrue(productId, pageable);
        return PagedResponse.of(reviews.map(ReviewDto::fromEntity));
    }
    
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to delete this review");
        }
        
        reviewRepository.delete(review);
    }
}
