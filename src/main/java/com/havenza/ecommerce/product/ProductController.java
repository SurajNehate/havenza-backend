package com.havenza.ecommerce.product;

import com.havenza.ecommerce.common.ApiResponse;
import com.havenza.ecommerce.common.PagedResponse;
import com.havenza.ecommerce.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductDto>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        
        PagedResponse<ProductDto> products = productService.getProducts(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductDto>> getProduct(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductBySlug(slug)));
    }
}
