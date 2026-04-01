package com.havenza.ecommerce.payment.dto;

import com.havenza.ecommerce.payment.PaymentEntity;
import com.havenza.ecommerce.payment.PaymentMethod;
import com.havenza.ecommerce.payment.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentDto {
    private Long id;
    private Long orderId;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public static PaymentDto fromEntity(PaymentEntity entity) {
        return PaymentDto.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getId())
                .method(entity.getMethod())
                .status(entity.getStatus())
                .transactionId(entity.getTransactionId())
                .amount(entity.getAmount())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
