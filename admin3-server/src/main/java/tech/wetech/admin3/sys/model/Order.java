package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单
 */
@Entity
@Table(name = "t_order")
@Comment("订单表")
public class Order extends BaseEntity {

    @Column(name = "order_no", nullable = false, unique = true, length = 32)
    @Comment("订单号")
    private String orderNo;

    @Column(name = "user_id", nullable = false)
    @Comment("用户ID")
    private Long userId;

    @Column(name = "product_id", nullable = false)
    @Comment("产品ID")
    private Long productId;

    @Column(name = "product_name", length = 64)
    @Comment("产品名称(冗余)")
    private String productName;

    @Column(precision = 10, scale = 2)
    @Comment("订单金额(元)")
    private BigDecimal amount;

    @Column(name = "pay_amount", precision = 10, scale = 2)
    @Comment("实付金额(元)")
    private BigDecimal payAmount;

    @Column(length = 32)
    @Comment("订单状态 pending-待付款 paid-已付款 reviewing-审核中 processing-办理中 completed-已完成 cancelled-已取消 refunding-退款中 refunded-已退款")
    private String status = "pending";

    @Column(name = "pay_status", length = 16)
    @Comment("支付状态 unpaid-未支付 paid-已支付 refunded-已退款")
    private String payStatus = "unpaid";

    @Column(name = "pay_time")
    @Comment("支付时间")
    private LocalDateTime payTime;

    @Column(name = "transaction_id", length = 64)
    @Comment("微信支付交易号")
    private String transactionId;

    @Column(name = "company_name", length = 128)
    @Comment("企业名称")
    private String companyName;

    @Column(name = "credit_code", length = 32)
    @Comment("统一社会信用代码")
    private String creditCode;

    @Column(name = "legal_person", length = 32)
    @Comment("法人姓名")
    private String legalPerson;

    @Column(name = "legal_phone", length = 20)
    @Comment("法人手机号")
    private String legalPhone;

    @Column(name = "legal_id_card", length = 32)
    @Comment("法人身份证号")
    private String legalIdCard;

    @Column(name = "form_data", columnDefinition = "TEXT")
    @Comment("表单数据(JSON)")
    private String formData;

    @Column(name = "user_coupon_id")
    @Comment("使用的优惠券ID")
    private Long userCouponId;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    @Comment("优惠金额(元)")
    private BigDecimal discountAmount;

    @Column(name = "assignee_id")
    @Comment("办理人员ID")
    private Long assigneeId;

    @Column(name = "assignee_name", length = 32)
    @Comment("办理人员姓名")
    private String assigneeName;

    @Column(name = "assign_time")
    @Comment("派单时间")
    private LocalDateTime assignTime;

    @Column(name = "complete_time")
    @Comment("完成时间")
    private LocalDateTime completeTime;

    @Column(columnDefinition = "TEXT")
    @Comment("备注")
    private String remark;

    @Column(name = "cancel_reason", length = 256)
    @Comment("取消原因")
    private String cancelReason;

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
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getPayAmount() { return payAmount; }
    public void setPayAmount(BigDecimal payAmount) { this.payAmount = payAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }
    public LocalDateTime getPayTime() { return payTime; }
    public void setPayTime(LocalDateTime payTime) { this.payTime = payTime; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCreditCode() { return creditCode; }
    public void setCreditCode(String creditCode) { this.creditCode = creditCode; }
    public String getLegalPerson() { return legalPerson; }
    public void setLegalPerson(String legalPerson) { this.legalPerson = legalPerson; }
    public String getLegalPhone() { return legalPhone; }
    public void setLegalPhone(String legalPhone) { this.legalPhone = legalPhone; }
    public String getLegalIdCard() { return legalIdCard; }
    public void setLegalIdCard(String legalIdCard) { this.legalIdCard = legalIdCard; }
    public String getFormData() { return formData; }
    public void setFormData(String formData) { this.formData = formData; }
    public Long getUserCouponId() { return userCouponId; }
    public void setUserCouponId(Long userCouponId) { this.userCouponId = userCouponId; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }
    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }
    public LocalDateTime getAssignTime() { return assignTime; }
    public void setAssignTime(LocalDateTime assignTime) { this.assignTime = assignTime; }
    public LocalDateTime getCompleteTime() { return completeTime; }
    public void setCompleteTime(LocalDateTime completeTime) { this.completeTime = completeTime; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
