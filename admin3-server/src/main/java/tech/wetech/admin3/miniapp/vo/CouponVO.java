package tech.wetech.admin3.miniapp.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券VO
 */
public class CouponVO {
    
    private Long id;
    private Long couponId; // 优惠券模板ID（用户优惠券时使用）
    private String name;
    private String type; // fixed-固定金额 percent-折扣
    private String typeText;
    private BigDecimal value;
    private BigDecimal minAmount;
    private BigDecimal maxDiscount;
    private String description;
    private String status; // unused-未使用 used-已使用 expired-已过期
    private String statusText;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime receiveTime;
    private LocalDateTime useTime;
    private Boolean canReceive; // 是否可领取（优惠券列表使用）
    private Integer remainCount; // 剩余数量
    private BigDecimal discountAmount; // 显示用的优惠金额

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTypeText() { return typeText; }
    public void setTypeText(String typeText) { this.typeText = typeText; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public BigDecimal getMinAmount() { return minAmount; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
    public BigDecimal getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public LocalDateTime getReceiveTime() { return receiveTime; }
    public void setReceiveTime(LocalDateTime receiveTime) { this.receiveTime = receiveTime; }
    public LocalDateTime getUseTime() { return useTime; }
    public void setUseTime(LocalDateTime useTime) { this.useTime = useTime; }
    public Boolean getCanReceive() { return canReceive; }
    public void setCanReceive(Boolean canReceive) { this.canReceive = canReceive; }
    public Integer getRemainCount() { return remainCount; }
    public void setRemainCount(Integer remainCount) { this.remainCount = remainCount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
}
