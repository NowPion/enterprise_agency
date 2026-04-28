package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 轮播图
 */
@Entity
@Table(name = "t_banner")
@Comment("轮播图表")
public class Banner extends BaseEntity {

    @Column(length = 128)
    @Comment("标题")
    private String title;

    @Column(nullable = false, length = 512)
    @Comment("图片URL")
    private String image;

    @Column(name = "link_type")
    @Comment("链接类型 0-无 1-页面 2-外链")
    private Integer linkType = 0;

    @Column(name = "link_url", length = 512)
    @Comment("链接地址")
    private String linkUrl;

    @Comment("排序值(越大越靠前)")
    private Integer sort = 0;

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
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Integer getLinkType() { return linkType; }
    public void setLinkType(Integer linkType) { this.linkType = linkType; }
    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
