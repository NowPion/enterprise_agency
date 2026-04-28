package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 公告
 */
@Entity
@Table(name = "t_notice")
@Comment("公告表")
public class Notice extends BaseEntity {

    @Column(nullable = false, length = 128)
    @Comment("公告标题")
    private String title;

    @Column(columnDefinition = "TEXT")
    @Comment("公告内容")
    private String content;

    @Column(name = "notice_type", length = 32)
    @Comment("公告类型 system-系统公告 activity-活动公告 policy-政策公告")
    private String noticeType = "system";

    @Column(name = "is_top")
    @Comment("是否置顶 0-否 1-是")
    private Integer isTop = 0;

    @Comment("排序值")
    private Integer sort = 0;

    @Comment("状态 0-下架 1-上架")
    private Integer status = 1;

    @Column(name = "publish_time")
    @Comment("发布时间")
    private LocalDateTime publishTime;

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
        if (this.publishTime == null) {
            this.publishTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getNoticeType() { return noticeType; }
    public void setNoticeType(String noticeType) { this.noticeType = noticeType; }
    public Integer getIsTop() { return isTop; }
    public void setIsTop(Integer isTop) { this.isTop = isTop; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getPublishTime() { return publishTime; }
    public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
