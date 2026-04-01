package com.havenza.ecommerce.cart;

import com.havenza.ecommerce.auth.UserDetailsImpl;
import com.havenza.ecommerce.cart.dto.AddToCartRequest;
import com.havenza.ecommerce.cart.dto.CartDto;
import com.havenza.ecommerce.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(userDetails.getId())));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(ApiResponse.success(cartService.addItemToCart(userDetails.getId(), request)));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDto>> updateQuantity(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(ApiResponse.success(cartService.updateItemQuantity(userDetails.getId(), itemId, quantity)));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success(cartService.removeItem(userDetails.getId(), itemId)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        cartService.clearCart(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared successfully"));
    }
}
