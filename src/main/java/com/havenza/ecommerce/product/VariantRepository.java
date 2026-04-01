package com.havenza.ecommerce.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VariantRepository extends JpaRepository<VariantEntity, Long> {
    Optional<VariantEntity> findBySku(String sku);
}
