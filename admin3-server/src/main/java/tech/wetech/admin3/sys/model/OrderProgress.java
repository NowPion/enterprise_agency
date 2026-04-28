package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 订单进度
 */
@Entity
@Table(name = "t_order_progress")
@Comment("订单进度表")
public class OrderProgress extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    @Comment("订单ID")
    private Long orderId;

    @Column(name = "order_no", length = 32)
    @Comment("订单号")
    private String orderNo;

    @Column(nullable = false, length = 64)
    @Comment("进度标题")
    private String title;

    @Column(columnDefinition = "TEXT")
    @Comment("进度描述")
    private String content;

    @Column(length = 32)
    @Comment("进度状态 pending-待处理 processing-处理中 completed-已完成")
    private String status = "completed";

    @Column(name = "operator_id")
    @Comment("操作人ID")
    private Long operatorId;

    @Column(name = "operator_name", length = 32)
    @Comment("操作人姓名")
    private String operatorName;

    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
