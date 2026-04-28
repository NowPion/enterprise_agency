package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 业务产品
 */
@Entity
@Table(name = "t_product")
@Comment("业务产品表")
public class Product extends BaseEntity {

    @Column(nullable = false, length = 64)
    @Comment("产品名称")
    private String name;

    @Column(length = 32)
    @Comment("产品编码")
    private String code;

    @Column(name = "category", length = 32)
    @Comment("分类 annual_report-年报 abnormal-异常解除 register-注册 cancel-注销")
    private String category;

    @Column(length = 512)
    @Comment("产品图片")
    private String image;

    @Column(columnDefinition = "TEXT")
    @Comment("产品描述")
    private String description;

    @Column(precision = 10, scale = 2)
    @Comment("原价(元)")
    private BigDecimal originalPrice;

    @Column(precision = 10, scale = 2)
    @Comment("现价(元)")
    private BigDecimal price;

    @Column(name = "process_days")
    @Comment("预计办理天数")
    private Integer processDays;

    @Column(columnDefinition = "TEXT")
    @Comment("办理须知")
    private String notice;

    @Column(columnDefinition = "TEXT")
    @Comment("所需材料说明")
    private String materials;

    @Comment("排序值(越大越靠前)")
    private Integer sort = 0;

    @Comment("状态 0-下架 1-上架")
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
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getProcessDays() { return processDays; }
    public void setProcessDays(Integer processDays) { this.processDays = processDays; }
    public String getNotice() { return notice; }
    public void setNotice(String notice) { this.notice = notice; }
    public String getMaterials() { return materials; }
    public void setMaterials(String materials) { this.materials = materials; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
