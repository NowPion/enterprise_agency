package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券模板
 */
@Entity
@Table(name = "t_coupon")
@Comment("优惠券模板表")
public class Coupon extends BaseEntity {

    @Column(nullable = false, length = 64)
    @Comment("优惠券名称")
    private String name;

    @Column(length = 32)
    @Comment("优惠券类型 fixed-固定金额 percent-折扣百分比")
    private String type = "fixed";

    @Column(precision = 10, scale = 2)
    @Comment("优惠金额(元)/折扣比例(如0.9表示9折)")
    private BigDecimal value;

    @Column(name = "min_amount", precision = 10, scale = 2)
    @Comment("最低消费金额(元)")
    private BigDecimal minAmount = BigDecimal.ZERO;

    @Column(name = "max_discount", precision = 10, scale = 2)
    @Comment("最大优惠金额(折扣券使用)")
    private BigDecimal maxDiscount;

    @Column(name = "total_count")
    @Comment("发放总量(-1表示不限)")
    private Integer totalCount = -1;

    @Column(name = "received_count")
    @Comment("已领取数量")
    private Integer receivedCount = 0;

    @Column(name = "used_count")
    @Comment("已使用数量")
    private Integer usedCount = 0;

    @Column(name = "per_limit")
    @Comment("每人限领数量")
    private Integer perLimit = 1;

    @Column(name = "valid_type", length = 16)
    @Comment("有效期类型 fixed-固定时间 days-领取后N天")
    private String validType = "fixed";

    @Column(name = "valid_days")
    @Comment("有效天数(validType=days时使用)")
    private Integer validDays;

    @Column(name = "start_time")
    @Comment("有效期开始时间")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    @Comment("有效期结束时间")
    private LocalDateTime endTime;

    @Column(name = "applicable_products", length = 512)
    @Comment("适用产品ID列表(JSON数组,空表示全部)")
    private String applicableProducts;

    @Column(length = 256)
    @Comment("使用说明")
    private String description;

    @Comment("状态 0-禁用 1-启用")
    private Integer status = 1;

    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public BigDecimal getMinAmount() { return minAmount; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
    public BigDecimal getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getReceivedCount() { return receivedCount; }
    public void setReceivedCount(Integer receivedCount) { this.receivedCount = receivedCount; }
    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    public Integer getPerLimit() { return perLimit; }
    public void setPerLimit(Integer perLimit) { this.perLimit = perLimit; }
    public String getValidType() { return validType; }
    public void setValidType(String validType) { this.validType = validType; }
    public Integer getValidDays() { return validDays; }
    public void setValidDays(Integer validDays) { this.validDays = validDays; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getApplicableProducts() { return applicableProducts; }
    public void setApplicableProducts(String applicableProducts) { this.applicableProducts = applicableProducts; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
