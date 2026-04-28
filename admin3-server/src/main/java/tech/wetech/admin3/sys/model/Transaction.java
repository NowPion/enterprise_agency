package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录
 */
@Entity
@Table(name = "t_transaction")
@Comment("交易记录表")
public class Transaction extends BaseEntity {

    @Column(name = "transaction_no", nullable = false, unique = true, length = 64)
    @Comment("交易流水号")
    private String transactionNo;

    @Column(name = "order_id")
    @Comment("关联订单ID")
    private Long orderId;

    @Column(name = "order_no", length = 32)
    @Comment("关联订单号")
    private String orderNo;

    @Column(name = "user_id")
    @Comment("用户ID")
    private Long userId;

    @Column(name = "transaction_type", length = 16)
    @Comment("交易类型 pay-支付 refund-退款")
    private String transactionType;

    @Column(precision = 10, scale = 2)
    @Comment("交易金额(元)")
    private BigDecimal amount;

    @Column(name = "pay_channel", length = 16)
    @Comment("支付渠道 wechat-微信支付")
    private String payChannel = "wechat";

    @Column(name = "wechat_transaction_id", length = 64)
    @Comment("微信支付交易号")
    private String wechatTransactionId;

    @Column(length = 16)
    @Comment("交易状态 pending-处理中 success-成功 failed-失败")
    private String status = "pending";

    @Column(name = "fail_reason", length = 256)
    @Comment("失败原因")
    private String failReason;

    @Column(length = 256)
    @Comment("备注")
    private String remark;

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
    public String getTransactionNo() { return transactionNo; }
    public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPayChannel() { return payChannel; }
    public void setPayChannel(String payChannel) { this.payChannel = payChannel; }
    public String getWechatTransactionId() { return wechatTransactionId; }
    public void setWechatTransactionId(String wechatTransactionId) { this.wechatTransactionId = wechatTransactionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
