package tech.wetech.admin3.miniapp.service.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tech.wetech.admin3.miniapp.service.IMiniappHomeService;
import tech.wetech.admin3.miniapp.vo.BannerVO;
import tech.wetech.admin3.miniapp.vo.ContentVO;
import tech.wetech.admin3.miniapp.vo.HomeDataVO;
import tech.wetech.admin3.miniapp.vo.ProductVO;
import tech.wetech.admin3.sys.model.Banner;
import tech.wetech.admin3.sys.model.Notice;
import tech.wetech.admin3.sys.model.Order;
import tech.wetech.admin3.sys.model.Product;
import tech.wetech.admin3.sys.repository.BannerRepository;
import tech.wetech.admin3.sys.repository.NoticeRepository;
import tech.wetech.admin3.sys.repository.OrderRepository;
import tech.wetech.admin3.sys.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MiniappHomeServiceImpl implements IMiniappHomeService {

    private final BannerRepository bannerRepository;
    private final ProductRepository productRepository;
    private final NoticeRepository noticeRepository;
    private final OrderRepository orderRepository;

    public MiniappHomeServiceImpl(BannerRepository bannerRepository, ProductRepository productRepository,
                                  NoticeRepository noticeRepository, OrderRepository orderRepository) {
        this.bannerRepository = bannerRepository;
        this.productRepository = productRepository;
        this.noticeRepository = noticeRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public HomeDataVO getHomeData() {
        HomeDataVO vo = new HomeDataVO();
        vo.setBanners(getBanners());
        vo.setHotProducts(getHotProducts());
        vo.setExampleProgress(getExampleProgress());
        vo.setNotices(getLatestNotices());
        return vo;
    }

    @Override
    public List<BannerVO> getBanners() {
        List<Banner> banners = bannerRepository.findByStatusOrderBySortAsc(1);
        return banners.stream().map(this::convertToBannerVO).collect(Collectors.toList());
    }

    @Override
    public List<HomeDataVO.ExampleProgressVO> getExampleProgress() {
        // 获取最近完成的订单作为示例进度展示（脱敏处理）
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "completeTime"));
        List<Order> orders = orderRepository.findByStatus("completed", pageRequest).getContent();
        
        return orders.stream().map(order -> {
            HomeDataVO.ExampleProgressVO vo = new HomeDataVO.ExampleProgressVO();
            // 企业名称脱敏：只显示前2个字和后1个字
            String companyName = order.getCompanyName();
            if (companyName != null && companyName.length() > 3) {
                vo.setCompanyName(companyName.substring(0, 2) + "***" + companyName.substring(companyName.length() - 1));
            } else {
                vo.setCompanyName("***企业");
            }
            vo.setBusinessType(order.getProductName());
            vo.setStatus("completed");
            vo.setStatusText("已完成");
            vo.setCreateTime(order.getCompleteTime());
            return vo;
        }).collect(Collectors.toList());
    }

    private List<ProductVO> getHotProducts() {
        // 获取上架的热门产品（前6个）
        PageRequest pageRequest = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "sort"));
        List<Product> products = productRepository.findByStatus(1, pageRequest).getContent();
        
        return products.stream().map(product -> {
            ProductVO vo = new ProductVO();
            vo.setId(product.getId());
            vo.setName(product.getName());
            vo.setCategory(product.getCategory());
            vo.setPrice(product.getPrice());
            vo.setOriginalPrice(product.getOriginalPrice());
            vo.setImage(product.getImage());
            vo.setDescription(product.getDescription());
            return vo;
        }).collect(Collectors.toList());
    }

    private List<ContentVO> getLatestNotices() {
        // 获取最新的3条公告
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createTime"));
        List<Notice> notices = noticeRepository.findByStatus(1, pageRequest).getContent();
        
        return notices.stream().map(notice -> {
            ContentVO vo = new ContentVO();
            vo.setId(notice.getId());
            vo.setTitle(notice.getTitle());
            vo.setCreateTime(notice.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    private BannerVO convertToBannerVO(Banner banner) {
        BannerVO vo = new BannerVO();
        vo.setId(banner.getId());
        vo.setTitle(banner.getTitle());
        vo.setImageUrl(banner.getImage());
        vo.setLinkUrl(banner.getLinkUrl());
        vo.setLinkType(banner.getLinkType());
        return vo;
    }
}
