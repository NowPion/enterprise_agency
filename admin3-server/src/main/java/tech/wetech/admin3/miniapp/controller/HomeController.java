package tech.wetech.admin3.miniapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.miniapp.ApiResult;
import tech.wetech.admin3.miniapp.service.IMiniappHomeService;
import tech.wetech.admin3.miniapp.vo.BannerVO;
import tech.wetech.admin3.miniapp.vo.HomeDataVO;

import java.util.List;

/**
 * 小程序首页接口
 */
@Tag(name = "小程序-首页", description = "小程序首页相关接口")
@RestController("miniappHomeController")
@RequestMapping("/minapp/home")
public class HomeController {

    private final IMiniappHomeService homeService;

    public HomeController(IMiniappHomeService homeService) {
        this.homeService = homeService;
    }

    @Operation(summary = "获取首页数据")
    @GetMapping("/data")
    public ApiResult<HomeDataVO> getHomeData() {
        return ApiResult.ok(homeService.getHomeData());
    }

    @Operation(summary = "获取轮播图列表")
    @GetMapping("/banners")
    public ApiResult<List<BannerVO>> getBanners() {
        return ApiResult.ok(homeService.getBanners());
    }

    @Operation(summary = "获取示例进度（展示用）")
    @GetMapping("/example-progress")
    public ApiResult<List<HomeDataVO.ExampleProgressVO>> getExampleProgress() {
        return ApiResult.ok(homeService.getExampleProgress());
    }
}
