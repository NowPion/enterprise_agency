package tech.wetech.admin3.miniapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.miniapp.ApiResult;
import tech.wetech.admin3.miniapp.service.IMiniappProductService;
import tech.wetech.admin3.miniapp.vo.PageVO;
import tech.wetech.admin3.miniapp.vo.ProductVO;

import java.util.List;

/**
 * 小程序产品接口
 */
@Tag(name = "小程序-产品", description = "小程序产品相关接口")
@RestController("miniappProductController")
@RequestMapping("/minapp/product")
public class ProductController {

    private final IMiniappProductService productService;

    public ProductController(IMiniappProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "获取产品列表")
    @GetMapping("/list")
    public ApiResult<PageVO<ProductVO>> getProductList(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResult.ok(productService.getProductList(category, page, size));
    }

    @Operation(summary = "获取产品详情")
    @GetMapping("/detail/{id}")
    public ApiResult<ProductVO> getProductDetail(@PathVariable Long id) {
        return ApiResult.ok(productService.getProductDetail(id));
    }

    @Operation(summary = "获取产品分类")
    @GetMapping("/categories")
    public ApiResult<List<String>> getCategories() {
        return ApiResult.ok(productService.getCategories());
    }
}
