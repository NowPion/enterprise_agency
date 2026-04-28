package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 常见问题
 */
@Entity
@Table(name = "t_faq")
@Comment("常见问题表")
public class Faq extends BaseEntity {

    @Column(nullable = false, length = 256)
    @Comment("问题标题")
    private String question;

    @Column(columnDefinition = "TEXT")
    @Comment("问题答案")
    private String answer;

    @Column(length = 32)
    @Comment("分类 annual_report-年报 abnormal-异常解除 register-注册 cancel-注销 general-通用")
    private String category = "general";

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
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
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
