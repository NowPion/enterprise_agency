package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 用户反馈
 */
@Entity
@Table(name = "t_feedback")
@Comment("用户反馈表")
public class Feedback extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    @Comment("用户ID")
    private Long userId;

    @Column(length = 32)
    @Comment("反馈类型 suggestion-建议 complaint-投诉 question-问题 other-其他")
    private String type = "other";

    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("反馈内容")
    private String content;

    @Column(length = 1024)
    @Comment("图片URL列表(JSON数组)")
    private String images;

    @Column(length = 64)
    @Comment("联系方式")
    private String contact;

    @Column(length = 16)
    @Comment("状态 pending-待处理 processing-处理中 resolved-已解决 closed-已关闭")
    private String status = "pending";

    @Column(columnDefinition = "TEXT")
    @Comment("回复内容")
    private String reply;

    @Column(name = "reply_time")
    @Comment("回复时间")
    private LocalDateTime replyTime;

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
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public LocalDateTime getReplyTime() { return replyTime; }
    public void setReplyTime(LocalDateTime replyTime) { this.replyTime = replyTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
