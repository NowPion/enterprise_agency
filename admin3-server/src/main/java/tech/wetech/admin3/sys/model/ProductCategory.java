package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 产品分类
 */
@Entity
@Table(name = "t_product_category")
@Comment("产品分类表")
public class ProductCategory extends BaseEntity {

    @Column(nullable = false, length = 64)
    @Comment("分类名称")
    private String name;

    @Column(nullable = false, unique = true, length = 32)
    @Comment("分类编码")
    private String code;

    @Column(length = 512)
    @Comment("分类图标")
    private String icon;

    @Comment("排序值(越大越靠前)")
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
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
