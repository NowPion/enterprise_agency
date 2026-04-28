package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款记录
 */
@Entity
@Table(name = "t_refund")
@Comment("退款记录表")
public class Refund extends BaseEntity {

    @Column(name = "refund_no", nullable = false, unique = true, length = 64)
    @Comment("退款单号")
    private String refundNo;

    @Column(name = "order_id", nullable = false)
    @Comment("订单ID")
    private Long orderId;

    @Column(name = "order_no", length = 32)
    @Comment("订单号")
    private String orderNo;

    @Column(name = "user_id")
    @Comment("用户ID")
    private Long userId;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    @Comment("退款金额(元)")
    private BigDecimal refundAmount;

    @Column(name = "order_amount", precision = 10, scale = 2)
    @Comment("订单原金额(元)")
    private BigDecimal orderAmount;

    @Column(name = "refund_reason", length = 256)
    @Comment("退款原因")
    private String refundReason;

    @Column(length = 16)
    @Comment("退款状态 pending-待处理 processing-处理中 success-成功 failed-失败 rejected-已拒绝")
    private String status = "pending";

    @Column(name = "wechat_refund_id", length = 64)
    @Comment("微信退款单号")
    private String wechatRefundId;

    @Column(name = "fail_reason", length = 256)
    @Comment("失败原因")
    private String failReason;

    @Column(name = "reject_reason", length = 256)
    @Comment("拒绝原因")
    private String rejectReason;

    @Column(name = "operator_id")
    @Comment("操作人ID")
    private Long operatorId;

    @Column(name = "operator_name", length = 32)
    @Comment("操作人姓名")
    private String operatorName;

    @Column(name = "refund_time")
    @Comment("退款成功时间")
    private LocalDateTime refundTime;

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
    public String getRefundNo() { return refundNo; }
    public void setRefundNo(String refundNo) { this.refundNo = refundNo; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    public BigDecimal getOrderAmount() { return orderAmount; }
    public void setOrderAmount(BigDecimal orderAmount) { this.orderAmount = orderAmount; }
    public String getRefundReason() { return refundReason; }
    public void setRefundReason(String refundReason) { this.refundReason = refundReason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getWechatRefundId() { return wechatRefundId; }
    public void setWechatRefundId(String wechatRefundId) { this.wechatRefundId = wechatRefundId; }
    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public LocalDateTime getRefundTime() { return refundTime; }
    public void setRefundTime(LocalDateTime refundTime) { this.refundTime = refundTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
