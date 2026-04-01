package com.havenza.ecommerce.order;

import com.havenza.ecommerce.auth.UserDetailsImpl;
import com.havenza.ecommerce.common.ApiResponse;
import com.havenza.ecommerce.common.PagedResponse;
import com.havenza.ecommerce.order.dto.OrderDto;
import com.havenza.ecommerce.order.dto.PlaceOrderRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> placeOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(orderService.placeOrder(userDetails.getId(), request), "Order placed successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<OrderDto>>> getOrders(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(userDetails.getId(), page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id, userDetails.getId())));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.cancelOrder(id, userDetails.getId()), "Order cancelled successfully"));
    }
}
