package com.havenza.ecommerce.product;

import com.havenza.ecommerce.common.PagedResponse;
import com.havenza.ecommerce.product.dto.CreateProductRequest;
import com.havenza.ecommerce.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public PagedResponse<ProductDto> getProducts(int page, int size, String sortParam) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("price_asc".equals(sortParam)) {
            sort = Sort.by(Sort.Direction.ASC, "basePrice");
        } else if ("price_desc".equals(sortParam)) {
            sort = Sort.by(Sort.Direction.DESC, "basePrice");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductEntity> productPage = productRepository.findByActiveTrue(pageable);

        Page<ProductDto> dtoPage = productPage.map(ProductDto::fromEntity);
        return PagedResponse.of(dtoPage);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductBySlug(String slug) {
        ProductEntity product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductDto.fromEntity(product);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductDto.fromEntity(product);
    }

    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        String slug = request.getName().toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        ProductEntity product = ProductEntity.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .category(category)
                .thumbnailUrl(request.getThumbnailUrl())
                .active(true)
                .variants(new ArrayList<>())
                .images(new ArrayList<>())
                .build();

        // Add variants
        if (request.getVariants() != null) {
            request.getVariants().forEach(vr -> {
                VariantEntity variant = VariantEntity.builder()
                        .product(product)
                        .name(vr.getName())
                        .sku(vr.getSku())
                        .price(vr.getPrice())
                        .stockQuantity(vr.getStockQuantity())
                        .imageUrl(vr.getImageUrl())
                        .attributes(vr.getAttributes())
                        .build();
                product.getVariants().add(variant);
            });
        }

        // Add images
        if (request.getImageUrls() != null) {
            AtomicInteger order = new AtomicInteger(0);
            request.getImageUrls().forEach(url -> {
                ProductImageEntity img = ProductImageEntity.builder()
                        .product(product)
                        .imageUrl(url)
                        .sortOrder(order.getAndIncrement())
                        .build();
                product.getImages().add(img);
            });
        }

        ProductEntity saved = productRepository.save(product);
        return ProductDto.fromEntity(saved);
    }

    @Transactional
    public ProductDto updateProduct(Long id, CreateProductRequest request) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());
        product.setCategory(category);
        product.setThumbnailUrl(request.getThumbnailUrl());

        // Replace variants
        // Merge variants by SKU to prevent unique constraint violation
        if (request.getVariants() != null) {
            java.util.List<VariantEntity> incomingVariants = new java.util.ArrayList<>();
            request.getVariants().forEach(vr -> {
                VariantEntity existingVariant = product.getVariants().stream()
                        .filter(v -> v.getSku().equals(vr.getSku()))
                        .findFirst()
                        .orElse(null);

                if (existingVariant != null) {
                    existingVariant.setName(vr.getName());
                    existingVariant.setPrice(vr.getPrice());
                    existingVariant.setStockQuantity(vr.getStockQuantity());
                    existingVariant.setImageUrl(vr.getImageUrl());
                    existingVariant.setAttributes(vr.getAttributes());
                    incomingVariants.add(existingVariant);
                } else {
                    incomingVariants.add(VariantEntity.builder()
                            .product(product)
                            .name(vr.getName())
                            .sku(vr.getSku())
                            .price(vr.getPrice())
                            .stockQuantity(vr.getStockQuantity())
                            .imageUrl(vr.getImageUrl())
                            .attributes(vr.getAttributes())
                            .build());
                }
            });
            product.getVariants().clear();
            product.getVariants().addAll(incomingVariants);
        }

        // Replace images
        product.getImages().clear();
        if (request.getImageUrls() != null) {
            AtomicInteger order = new AtomicInteger(0);
            request.getImageUrls().forEach(url -> {
                ProductImageEntity img = ProductImageEntity.builder()
                        .product(product)
                        .imageUrl(url)
                        .sortOrder(order.getAndIncrement())
                        .build();
                product.getImages().add(img);
            });
        }

        ProductEntity saved = productRepository.save(product);
        return ProductDto.fromEntity(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }
}
