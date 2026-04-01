package com.havenza.ecommerce.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class CreateProductRequest {
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Long categoryId;
    private String thumbnailUrl;
    private List<String> imageUrls;
    private List<VariantRequest> variants;

    @Data
    public static class VariantRequest {
        private String name;
        private String sku;
        private BigDecimal price;
        private Integer stockQuantity;
        private String imageUrl;
        private Map<String, String> attributes;
    }
}
