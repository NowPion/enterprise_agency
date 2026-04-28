package tech.wetech.admin3.miniapp.service;

import tech.wetech.admin3.miniapp.vo.CouponVO;
import tech.wetech.admin3.miniapp.vo.PageVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序优惠券服务接口
 */
public interface IMiniappCouponService {
    
    /**
     * 获取可领取的优惠券列表
     */
    List<CouponVO> getAvailableCoupons(Long userId);
    
    /**
     * 领取优惠券
     */
    CouponVO receiveCoupon(Long userId, Long couponId);
    
    /**
     * 获取我的优惠券列表
     * @param status unused-未使用 used-已使用 expired-已过期，null表示全部
     */
    PageVO<CouponVO> getMyCoupons(Long userId, String status, int page, int size);
    
    /**
     * 获取订单可用的优惠券列表
     */
    List<CouponVO> getAvailableForOrder(Long userId, BigDecimal orderAmount, Long productId);
    
    /**
     * 获取未使用优惠券数量
     */
    long getUnusedCount(Long userId);
    
    /**
     * 计算优惠金额
     */
    BigDecimal calculateDiscount(Long userCouponId, BigDecimal orderAmount);
}
