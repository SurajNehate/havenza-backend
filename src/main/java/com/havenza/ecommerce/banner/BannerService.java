package com.havenza.ecommerce.banner;

import com.havenza.ecommerce.banner.dto.BannerDto;
import com.havenza.ecommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    @Transactional(readOnly = true)
    public List<BannerDto> getActiveBanners() {
        return bannerRepository.findByActiveTrue(Sort.by(Sort.Direction.ASC, "sortOrder"))
                .stream()
                .map(BannerDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BannerDto> getAllBanners() {
        return bannerRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder"))
                .stream()
                .map(BannerDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public BannerDto createBanner(BannerDto request) {
        BannerEntity banner = BannerEntity.builder()
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .linkUrl(request.getLinkUrl())
                .active(request.isActive())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();

        return BannerDto.fromEntity(bannerRepository.save(banner));
    }

    @Transactional
    public BannerDto updateBanner(Long id, BannerDto request) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found"));
        banner.setTitle(request.getTitle());
        banner.setImageUrl(request.getImageUrl());
        banner.setLinkUrl(request.getLinkUrl());
        banner.setActive(request.isActive());
        banner.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        return BannerDto.fromEntity(bannerRepository.save(banner));
    }

    @Transactional
    public void deleteBanner(Long id) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found"));
        bannerRepository.delete(banner);
    }
}
