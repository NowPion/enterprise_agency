package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品年度价格
 */
@Entity
@Table(name = "t_product_year_price")
@Comment("产品年度价格表")
public class ProductYearPrice extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    @Comment("产品ID")
    private Long productId;

    @Column(nullable = false, length = 32)
    @Comment("年度 如: 1年 2年 3年 或 2024年 2025年")
    private String year;

    @Column(precision = 10, scale = 2)
    @Comment("原价(元)")
    private BigDecimal originalPrice;

    @Column(precision = 10, scale = 2, nullable = false)
    @Comment("现价(元)")
    private BigDecimal price;

    @Comment("排序值(越小越靠前)")
    private Integer sort = 0;

    @Comment("状态 0-禁用 1-启用")
    private Integer status = 1;

    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
