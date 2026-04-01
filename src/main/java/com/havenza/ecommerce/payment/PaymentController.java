package com.havenza.ecommerce.payment;

import com.havenza.ecommerce.auth.UserDetailsImpl;
import com.havenza.ecommerce.common.ApiResponse;
import com.havenza.ecommerce.payment.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponse<PaymentDto>> processPayment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.processPayment(orderId, userDetails.getId()), "Payment processed"));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentStatus(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentStatus(orderId, userDetails.getId())));
    }
}
