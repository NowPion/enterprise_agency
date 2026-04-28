package tech.wetech.admin3.miniapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.miniapp.ApiResult;
import tech.wetech.admin3.miniapp.auth.MiniappAuth;
import tech.wetech.admin3.miniapp.service.IMiniappCouponService;
import tech.wetech.admin3.miniapp.vo.CouponVO;
import tech.wetech.admin3.miniapp.vo.PageVO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序优惠券接口
 */
@Tag(name = "小程序-优惠券", description = "小程序优惠券相关接口")
@RestController("miniappCouponController")
@RequestMapping("/minapp/coupon")
public class CouponController {

    private final IMiniappCouponService couponService;

    public CouponController(IMiniappCouponService couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "获取可领取的优惠券列表")
    @GetMapping("/available")
    public ApiResult<List<CouponVO>> getAvailableCoupons(@MiniappAuth Long userId) {
        return ApiResult.ok(couponService.getAvailableCoupons(userId));
    }

    @Operation(summary = "领取优惠券")
    @PostMapping("/receive/{couponId}")
    public ApiResult<CouponVO> receiveCoupon(@MiniappAuth Long userId, @PathVariable Long couponId) {
        return ApiResult.ok("领取成功", couponService.receiveCoupon(userId, couponId));
    }

    @Operation(summary = "获取我的优惠券列表")
    @GetMapping("/my")
    public ApiResult<PageVO<CouponVO>> getMyCoupons(
            @MiniappAuth Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResult.ok(couponService.getMyCoupons(userId, status, page, size));
    }

    @Operation(summary = "获取订单可用的优惠券")
    @GetMapping("/order-available")
    public ApiResult<List<CouponVO>> getAvailableForOrder(
            @MiniappAuth Long userId,
            @RequestParam BigDecimal orderAmount,
            @RequestParam(required = false) Long productId) {
        return ApiResult.ok(couponService.getAvailableForOrder(userId, orderAmount, productId));
    }

    @Operation(summary = "获取未使用优惠券数量")
    @GetMapping("/unused-count")
    public ApiResult<Map<String, Object>> getUnusedCount(@MiniappAuth Long userId) {
        long count = couponService.getUnusedCount(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return ApiResult.ok(result);
    }

    @Operation(summary = "计算优惠金额")
    @GetMapping("/calculate")
    public ApiResult<Map<String, Object>> calculateDiscount(
            @MiniappAuth Long userId,
            @RequestParam Long userCouponId,
            @RequestParam BigDecimal orderAmount) {
        BigDecimal discount = couponService.calculateDiscount(userCouponId, orderAmount);
        Map<String, Object> result = new HashMap<>();
        result.put("discount", discount);
        result.put("payAmount", orderAmount.subtract(discount));
        return ApiResult.ok(result);
    }
}
