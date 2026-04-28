package tech.wetech.admin3.miniapp.service.impl;

import org.springframework.stereotype.Service;

import tech.wetech.admin3.miniapp.service.IHomeService;
import tech.wetech.admin3.miniapp.vo.BannerVO;

import tech.wetech.admin3.miniapp.vo.HomeStatsVO;
import tech.wetech.admin3.miniapp.vo.PageVO;
import tech.wetech.admin3.sys.model.Banner;

import tech.wetech.admin3.sys.repository.*;

import java.util.List;

/**
 * 小程序首页服务实现
 */
@Service
public class HomeServiceImpl implements IHomeService {

    private final BannerRepository bannerRepository;


    public HomeServiceImpl(BannerRepository bannerRepository,
                           MiniappUserRepository userRepository) {
        this.bannerRepository = bannerRepository;

    }

    @Override
    public PageVO<BannerVO> getBanners() {
        List<Banner> banners = bannerRepository.findByStatusOrderBySortAsc(1);
        List<BannerVO> list = banners.stream().map(this::convertToBannerVO).toList();
        return new PageVO<>(list, (long) list.size(), 1, list.size());
    }

  @Override
  public HomeStatsVO getStats() {
    return null;
  }


  private BannerVO convertToBannerVO(Banner banner) {
        BannerVO vo = new BannerVO();
        vo.setId(banner.getId());
        vo.setTitle(banner.getTitle());
        vo.setImageUrl(banner.getImage());
        vo.setLinkType(banner.getLinkType());
        vo.setLinkUrl(banner.getLinkUrl());
        return vo;
    }


}
