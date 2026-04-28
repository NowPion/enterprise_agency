package tech.wetech.admin3.miniapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.miniapp.ApiResult;
import tech.wetech.admin3.miniapp.service.IMiniappContentService;
import tech.wetech.admin3.miniapp.vo.ContentVO;
import tech.wetech.admin3.miniapp.vo.PageVO;
import tech.wetech.admin3.sys.model.MiniappUser;
import tech.wetech.admin3.sys.model.Order;
import tech.wetech.admin3.sys.repository.MiniappUserRepository;
import tech.wetech.admin3.sys.repository.OrderRepository;
import tech.wetech.admin3.sys.service.SystemConfigService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 小程序内容接口
 */
@Tag(name = "小程序-内容", description = "小程序内容相关接口（公告、FAQ、文章）")
@RestController("miniappContentController")
@RequestMapping("/minapp/content")
public class ContentController {

    private final IMiniappContentService contentService;
    private final SystemConfigService systemConfigService;
    private final OrderRepository orderRepository;
    private final MiniappUserRepository miniappUserRepository;

    public ContentController(IMiniappContentService contentService, SystemConfigService systemConfigService,
                             OrderRepository orderRepository, MiniappUserRepository miniappUserRepository) {
        this.contentService = contentService;
        this.systemConfigService = systemConfigService;
        this.orderRepository = orderRepository;
        this.miniappUserRepository = miniappUserRepository;
    }

    @Operation(summary = "获取办理动态")
    @GetMapping("/dynamics")
    public ApiResult<List<Map<String, String>>> getDynamics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {
        // 获取订单（按创建时间倒序分页）
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        var orderPage = orderRepository.findAll(pageable);
        var orders = orderPage.getContent();
        
        List<Map<String, String>> dynamics = new ArrayList<>();
        for (Order order : orders) {
            if (order.getCompanyName() == null || order.getCompanyName().isEmpty()) {
                continue;
            }
            // 获取用户手机号尾号
            String phoneTail = "****";
            MiniappUser user = miniappUserRepository.findById(order.getUserId()).orElse(null);
            if (user != null && user.getPhone() != null && user.getPhone().length() >= 4) {
                phoneTail = user.getPhone().substring(user.getPhone().length() - 4);
            }
            // 脱敏企业名称
            String companyName = maskCompanyName(order.getCompanyName());
            // 产品名称
            String productName = order.getProductName() != null ? order.getProductName() : "业务办理";
            
            dynamics.add(Map.of(
                "phoneTail", phoneTail,
                "companyName", companyName,
                "productName", productName
            ));
        }
        return ApiResult.ok(dynamics);
    }
    
    /**
     * 脱敏企业名称，保留前后各3个字，中间用***代替
     */
    private String maskCompanyName(String name) {
        if (name == null || name.length() <= 6) {
            return name;
        }
        return name.substring(0, 3) + "***" + name.substring(name.length() - 3);
    }

    @Operation(summary = "获取用户协议")
    @GetMapping("/agreement")
    public ApiResult<Map<String, String>> getAgreement() {
        String content = systemConfigService.getValue("app.user_agreement", "");
        return ApiResult.ok(Map.of("content", content));
    }

    @Operation(summary = "获取隐私政策")
    @GetMapping("/privacy")
    public ApiResult<Map<String, String>> getPrivacy() {
        String content = systemConfigService.getValue("app.privacy_policy", "");
        return ApiResult.ok(Map.of("content", content));
    }

    @Operation(summary = "获取公告列表")
    @GetMapping("/notice/list")
    public ApiResult<PageVO<ContentVO>> getNoticeList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResult.ok(contentService.getNoticeList(page, size));
    }

    @Operation(summary = "获取公告详情")
    @GetMapping("/notice/detail/{id}")
    public ApiResult<ContentVO> getNoticeDetail(@PathVariable Long id) {
        return ApiResult.ok(contentService.getNoticeDetail(id));
    }

    @Operation(summary = "获取FAQ列表")
    @GetMapping("/faq/list")
    public ApiResult<PageVO<ContentVO>> getFaqList(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResult.ok(contentService.getFaqList(category, page, size));
    }

    @Operation(summary = "获取文章列表")
    @GetMapping("/article/list")
    public ApiResult<PageVO<ContentVO>> getArticleList(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResult.ok(contentService.getArticleList(category, page, size));
    }

    @Operation(summary = "获取文章详情")
    @GetMapping("/article/detail/{id}")
    public ApiResult<ContentVO> getArticleDetail(@PathVariable Long id) {
        return ApiResult.ok(contentService.getArticleDetail(id));
    }
}
