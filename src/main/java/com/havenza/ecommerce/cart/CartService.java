package com.havenza.ecommerce.cart;

import com.havenza.ecommerce.auth.UserEntity;
import com.havenza.ecommerce.auth.UserRepository;
import com.havenza.ecommerce.cart.dto.AddToCartRequest;
import com.havenza.ecommerce.cart.dto.CartDto;
import com.havenza.ecommerce.common.exception.BusinessRuleException;
import com.havenza.ecommerce.common.exception.ResourceNotFoundException;
import com.havenza.ecommerce.common.exception.UnauthorizedException;
import com.havenza.ecommerce.product.VariantEntity;
import com.havenza.ecommerce.product.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final VariantRepository variantRepository;

    @Transactional
    public CartDto getCart(Long userId) {
        CartEntity cart = getOrCreateCart(userId);
        return CartDto.fromEntity(cart);
    }

    @Transactional
    public CartDto addItemToCart(Long userId, AddToCartRequest request) {
        CartEntity cart = getOrCreateCart(userId);
        VariantEntity variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        Optional<CartItemEntity> existingItem = cart.getItems().stream()
                .filter(item -> item.getVariant().getId().equals(variant.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItemEntity item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            if (variant.getStockQuantity() != null && newQuantity > variant.getStockQuantity()) {
                throw new BusinessRuleException("Not enough stock. Available: " + variant.getStockQuantity());
            }
            item.setQuantity(newQuantity);
        } else {
            if (variant.getStockQuantity() != null && request.getQuantity() > variant.getStockQuantity()) {
                throw new BusinessRuleException("Not enough stock. Available: " + variant.getStockQuantity());
            }
            CartItemEntity newItem = CartItemEntity.builder()
                    .cart(cart)
                    .variant(variant)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        return CartDto.fromEntity(cartRepository.save(cart));
    }

    @Transactional
    public CartDto updateItemQuantity(Long userId, Long itemId, int quantity) {
        CartEntity cart = getOrCreateCart(userId);
        CartItemEntity item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new UnauthorizedException("Item does not belong to user's cart");
        }

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            if (item.getVariant().getStockQuantity() != null && quantity > item.getVariant().getStockQuantity()) {
                throw new BusinessRuleException("Not enough stock. Available: " + item.getVariant().getStockQuantity());
            }
            item.setQuantity(quantity);
        }

        return CartDto.fromEntity(cartRepository.save(cart));
    }

    @Transactional
    public CartDto removeItem(Long userId, Long itemId) {
        return updateItemQuantity(userId, itemId, 0);
    }

    @Transactional
    public void clearCart(Long userId) {
        CartEntity cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public CartEntity getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            CartEntity newCart = CartEntity.builder()
                    .user(user)
                    .build();
            return cartRepository.save(newCart);
        });
    }
}
