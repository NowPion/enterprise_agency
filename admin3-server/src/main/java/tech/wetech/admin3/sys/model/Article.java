package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 文章/说明
 */
@Entity
@Table(name = "t_article")
@Comment("文章表")
public class Article extends BaseEntity {

    @Column(nullable = false, length = 128)
    @Comment("文章标题")
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    @Comment("文章内容(富文本)")
    private String content;

    @Column(length = 512)
    @Comment("封面图片")
    private String cover;

    @Column(length = 512)
    @Comment("摘要")
    private String summary;

    @Column(name = "article_type", length = 32)
    @Comment("文章类型 guide-办理指南 policy-政策说明 help-帮助文档")
    private String articleType = "guide";

    @Column(length = 32)
    @Comment("关联分类 annual_report/abnormal/register/cancel")
    private String category;

    @Comment("排序值")
    private Integer sort = 0;

    @Comment("状态 0-下架 1-上架")
    private Integer status = 1;

    @Column(name = "view_count")
    @Comment("浏览次数")
    private Integer viewCount = 0;

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
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getArticleType() { return articleType; }
    public void setArticleType(String articleType) { this.articleType = articleType; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
