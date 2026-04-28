package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券
 */
@Entity
@Table(name = "t_user_coupon")
@Comment("用户优惠券表")
public class UserCoupon extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    @Comment("用户ID")
    private Long userId;

    @Column(name = "coupon_id", nullable = false)
    @Comment("优惠券模板ID")
    private Long couponId;

    @Column(name = "coupon_name", length = 64)
    @Comment("优惠券名称(冗余)")
    private String couponName;

    @Column(name = "coupon_type", length = 32)
    @Comment("优惠券类型 fixed-固定金额 percent-折扣百分比")
    private String couponType;

    @Column(precision = 10, scale = 2)
    @Comment("优惠金额/折扣比例")
    private BigDecimal value;

    @Column(name = "min_amount", precision = 10, scale = 2)
    @Comment("最低消费金额")
    private BigDecimal minAmount;

    @Column(name = "max_discount", precision = 10, scale = 2)
    @Comment("最大优惠金额")
    private BigDecimal maxDiscount;

    @Column(length = 16)
    @Comment("状态 unused-未使用 used-已使用 expired-已过期")
    private String status = "unused";

    @Column(name = "order_id")
    @Comment("使用的订单ID")
    private Long orderId;

    @Column(name = "order_no", length = 32)
    @Comment("使用的订单号")
    private String orderNo;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    @Comment("实际优惠金额")
    private BigDecimal discountAmount;

    @Column(name = "start_time")
    @Comment("有效期开始时间")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    @Comment("有效期结束时间")
    private LocalDateTime endTime;

    @Column(name = "use_time")
    @Comment("使用时间")
    private LocalDateTime useTime;

    @Column(name = "receive_time")
    @Comment("领取时间")
    private LocalDateTime receiveTime;

    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
        this.receiveTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }
    public String getCouponType() { return couponType; }
    public void setCouponType(String couponType) { this.couponType = couponType; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public BigDecimal getMinAmount() { return minAmount; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
    public BigDecimal getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public LocalDateTime getUseTime() { return useTime; }
    public void setUseTime(LocalDateTime useTime) { this.useTime = useTime; }
    public LocalDateTime getReceiveTime() { return receiveTime; }
    public void setReceiveTime(LocalDateTime receiveTime) { this.receiveTime = receiveTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
