package tech.wetech.admin3.sys.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.Banner;
import tech.wetech.admin3.sys.repository.BannerRepository;

import java.util.List;

@Service
public class BannerService {

    private final BannerRepository bannerRepository;

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    public List<Banner> findAll() {
        return bannerRepository.findAll(Sort.by(Sort.Direction.ASC, "sort"));
    }

    public List<Banner> findEnabled() {
        return bannerRepository.findByStatusOrderBySortAsc(1);
    }

    public Banner findById(Long id) {
        return bannerRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST));
    }

    @Transactional
    public Banner create(Banner banner) {
        return bannerRepository.save(banner);
    }

    @Transactional
    public Banner update(Long id, Banner banner) {
        Banner existing = findById(id);
        if (banner.getTitle() != null) existing.setTitle(banner.getTitle());
        if (banner.getImage() != null) existing.setImage(banner.getImage());
        if (banner.getLinkType() != null) existing.setLinkType(banner.getLinkType());
        if (banner.getLinkUrl() != null) existing.setLinkUrl(banner.getLinkUrl());
        if (banner.getSort() != null) existing.setSort(banner.getSort());
        if (banner.getStatus() != null) existing.setStatus(banner.getStatus());
        return bannerRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }
}
