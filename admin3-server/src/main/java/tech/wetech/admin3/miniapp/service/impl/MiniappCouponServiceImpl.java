package tech.wetech.admin3.miniapp.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.miniapp.service.IMiniappCouponService;
import tech.wetech.admin3.miniapp.vo.CouponVO;
import tech.wetech.admin3.miniapp.vo.PageVO;
import tech.wetech.admin3.sys.model.Coupon;
import tech.wetech.admin3.sys.model.UserCoupon;
import tech.wetech.admin3.sys.repository.CouponRepository;
import tech.wetech.admin3.sys.repository.UserCouponRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MiniappCouponServiceImpl implements IMiniappCouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public MiniappCouponServiceImpl(CouponRepository couponRepository, UserCouponRepository userCouponRepository) {
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
    }

    @Override
    public List<CouponVO> getAvailableCoupons(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> coupons = couponRepository.findAvailableCoupons(now);
        
        return coupons.stream().map(coupon -> {
            CouponVO vo = convertCouponToVO(coupon);
            // 检查用户是否可领取
            long receivedCount = userCouponRepository.countByUserIdAndCouponId(userId, coupon.getId());
            vo.setCanReceive(receivedCount < coupon.getPerLimit());
            // 计算剩余数量
            if (coupon.getTotalCount() == -1) {
                vo.setRemainCount(-1); // 不限量
            } else {
                vo.setRemainCount(coupon.getTotalCount() - coupon.getReceivedCount());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CouponVO receiveCoupon(Long userId, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "优惠券不存在"));
        
        // 检查优惠券状态
        if (coupon.getStatus() != 1) {
            throw new BusinessException(CommonResultStatus.FAIL, "优惠券已下架");
        }
        
        // 检查是否还有库存
        if (coupon.getTotalCount() != -1 && coupon.getReceivedCount() >= coupon.getTotalCount()) {
            throw new BusinessException(CommonResultStatus.FAIL, "优惠券已领完");
        }
        
        // 检查有效期
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getEndTime() != null && coupon.getEndTime().isBefore(now)) {
            throw new BusinessException(CommonResultStatus.FAIL, "优惠券已过期");
        }
        
        // 检查用户领取数量
        long receivedCount = userCouponRepository.countByUserIdAndCouponId(userId, couponId);
        if (receivedCount >= coupon.getPerLimit()) {
            throw new BusinessException(CommonResultStatus.FAIL, "已达到领取上限");
        }
        
        // 创建用户优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setCouponName(coupon.getName());
        userCoupon.setCouponType(coupon.getType());
        userCoupon.setValue(coupon.getValue());
        userCoupon.setMinAmount(coupon.getMinAmount());
        userCoupon.setMaxDiscount(coupon.getMaxDiscount());
        userCoupon.setStatus("unused");
        
        // 设置有效期
        if ("days".equals(coupon.getValidType())) {
            userCoupon.setStartTime(now);
            userCoupon.setEndTime(now.plusDays(coupon.getValidDays()));
        } else {
            userCoupon.setStartTime(coupon.getStartTime());
            userCoupon.setEndTime(coupon.getEndTime());
        }
        
        userCoupon = userCouponRepository.save(userCoupon);
        
        // 更新优惠券领取数量
        coupon.setReceivedCount(coupon.getReceivedCount() + 1);
        couponRepository.save(coupon);
        
        return convertUserCouponToVO(userCoupon);
    }

    @Override
    public PageVO<CouponVO> getMyCoupons(Long userId, String status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<UserCoupon> couponPage;
        
        if (status != null && !status.isEmpty()) {
            couponPage = userCouponRepository.findByUserIdAndStatus(userId, status, pageRequest);
        } else {
            couponPage = userCouponRepository.findByUserId(userId, pageRequest);
        }
        
        // 检查并更新过期状态
        LocalDateTime now = LocalDateTime.now();
        List<CouponVO> list = couponPage.getContent().stream().map(uc -> {
            // 如果未使用且已过期，更新状态
            if ("unused".equals(uc.getStatus()) && uc.getEndTime() != null && uc.getEndTime().isBefore(now)) {
                uc.setStatus("expired");
                userCouponRepository.save(uc);
            }
            return convertUserCouponToVO(uc);
        }).collect(Collectors.toList());
        
        return new PageVO<>(list, couponPage.getTotalElements());
    }

    @Override
    public List<CouponVO> getAvailableForOrder(Long userId, BigDecimal orderAmount, Long productId) {
        LocalDateTime now = LocalDateTime.now();
        List<UserCoupon> coupons = userCouponRepository.findAvailableForOrder(userId, now, orderAmount);
        
        return coupons.stream()
                .map(this::convertUserCouponToVO)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnusedCount(Long userId) {
        return userCouponRepository.countByUserIdAndStatus(userId, "unused");
    }

    @Override
    public BigDecimal calculateDiscount(Long userCouponId, BigDecimal orderAmount) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "优惠券不存在"));
        
        if (!"unused".equals(userCoupon.getStatus())) {
            throw new BusinessException(CommonResultStatus.FAIL, "优惠券不可用");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (userCoupon.getEndTime() != null && userCoupon.getEndTime().isBefore(now)) {
            throw new BusinessException(CommonResultStatus.FAIL, "优惠券已过期");
        }
        
        if (userCoupon.getMinAmount() != null && orderAmount.compareTo(userCoupon.getMinAmount()) < 0) {
            throw new BusinessException(CommonResultStatus.FAIL, "未达到最低消费金额");
        }
        
        BigDecimal discount;
        if ("fixed".equals(userCoupon.getCouponType())) {
            // 固定金额优惠
            discount = userCoupon.getValue();
        } else {
            // 折扣优惠
            discount = orderAmount.multiply(BigDecimal.ONE.subtract(userCoupon.getValue()))
                    .setScale(2, RoundingMode.HALF_UP);
            // 检查最大优惠金额
            if (userCoupon.getMaxDiscount() != null && discount.compareTo(userCoupon.getMaxDiscount()) > 0) {
                discount = userCoupon.getMaxDiscount();
            }
        }
        
        // 优惠金额不能超过订单金额
        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }
        
        return discount;
    }

    private CouponVO convertCouponToVO(Coupon coupon) {
        CouponVO vo = new CouponVO();
        vo.setId(coupon.getId());
        vo.setCouponId(coupon.getId());
        vo.setName(coupon.getName());
        vo.setType(coupon.getType());
        vo.setTypeText("fixed".equals(coupon.getType()) ? "满减券" : "折扣券");
        vo.setValue(coupon.getValue());
        vo.setMinAmount(coupon.getMinAmount());
        vo.setMaxDiscount(coupon.getMaxDiscount());
        vo.setDescription(coupon.getDescription());
        vo.setStartTime(coupon.getStartTime());
        vo.setEndTime(coupon.getEndTime());
        return vo;
    }

    private CouponVO convertUserCouponToVO(UserCoupon userCoupon) {
        CouponVO vo = new CouponVO();
        vo.setId(userCoupon.getId());
        vo.setCouponId(userCoupon.getCouponId());
        vo.setName(userCoupon.getCouponName());
        vo.setType(userCoupon.getCouponType());
        vo.setTypeText("fixed".equals(userCoupon.getCouponType()) ? "满减券" : "折扣券");
        vo.setValue(userCoupon.getValue());
        vo.setMinAmount(userCoupon.getMinAmount());
        vo.setMaxDiscount(userCoupon.getMaxDiscount());
        vo.setStatus(userCoupon.getStatus());
        vo.setStatusText(getStatusText(userCoupon.getStatus()));
        vo.setStartTime(userCoupon.getStartTime());
        vo.setEndTime(userCoupon.getEndTime());
        vo.setReceiveTime(userCoupon.getReceiveTime());
        vo.setUseTime(userCoupon.getUseTime());
        // 计算显示用的优惠金额（固定金额券直接显示value，折扣券显示折扣比例）
        if ("fixed".equals(userCoupon.getCouponType())) {
            vo.setDiscountAmount(userCoupon.getValue());
        } else {
            // 折扣券显示折扣值，如0.9表示9折
            vo.setDiscountAmount(userCoupon.getValue());
        }
        return vo;
    }

    private String getStatusText(String status) {
        return switch (status) {
            case "unused" -> "未使用";
            case "used" -> "已使用";
            case "expired" -> "已过期";
            default -> "未知";
        };
    }
}
