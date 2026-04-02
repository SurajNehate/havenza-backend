package com.havenza.ecommerce.wishlist;

import com.havenza.ecommerce.auth.UserEntity;
import com.havenza.ecommerce.auth.UserRepository;
import com.havenza.ecommerce.common.PagedResponse;
import com.havenza.ecommerce.common.exception.DuplicateResourceException;
import com.havenza.ecommerce.common.exception.ResourceNotFoundException;
import com.havenza.ecommerce.product.ProductEntity;
import com.havenza.ecommerce.product.ProductRepository;
import com.havenza.ecommerce.wishlist.dto.WishlistDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public PagedResponse<WishlistDto> getUserWishlist(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<WishlistEntity> wishlists = wishlistRepository.findByUserId(userId, pageable);
        return PagedResponse.of(wishlists.map(WishlistDto::fromEntity));
    }

    @Transactional
    public WishlistDto addToWishlist(Long userId, Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new DuplicateResourceException("Product is already in the wishlist");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        WishlistEntity wishlistEntity = WishlistEntity.builder()
                .user(user)
                .product(product)
                .build();

        return WishlistDto.fromEntity(wishlistRepository.save(wishlistEntity));
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        WishlistEntity wishlistEntity = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in wishlist"));

        wishlistRepository.delete(wishlistEntity);
    }
}
