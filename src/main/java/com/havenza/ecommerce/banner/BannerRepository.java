package com.havenza.ecommerce.banner;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<BannerEntity, Long> {
    List<BannerEntity> findByActiveTrue(Sort sort);
}
