package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 发票
 */
@Entity
@Table(name = "t_invoice")
@Comment("发票表")
public class Invoice extends BaseEntity {

    @Column(name = "invoice_no", unique = true, length = 64)
    @Comment("发票号码")
    private String invoiceNo;

    @Column(name = "order_id", nullable = false)
    @Comment("订单ID")
    private Long orderId;

    @Column(name = "order_no", length = 32)
    @Comment("订单号")
    private String orderNo;

    @Column(name = "user_id")
    @Comment("用户ID")
    private Long userId;

    @Column(name = "invoice_type", length = 16)
    @Comment("发票类型 personal-个人 company-企业")
    private String invoiceType = "personal";

    @Column(name = "title_type", length = 16)
    @Comment("抬头类型 personal-个人 company-公司")
    private String titleType = "personal";

    @Column(length = 128)
    @Comment("发票抬头")
    private String title;

    @Column(name = "tax_no", length = 32)
    @Comment("税号")
    private String taxNo;

    @Column(precision = 10, scale = 2)
    @Comment("发票金额(元)")
    private BigDecimal amount;

    @Column(length = 64)
    @Comment("接收邮箱")
    private String email;

    @Column(length = 16)
    @Comment("状态 pending-待开票 issued-已开票 failed-开票失败")
    private String status = "pending";

    @Column(name = "invoice_url", length = 512)
    @Comment("发票文件URL")
    private String invoiceUrl;

    @Column(name = "fail_reason", length = 256)
    @Comment("失败原因")
    private String failReason;

    @Column(name = "issue_time")
    @Comment("开票时间")
    private LocalDateTime issueTime;

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
    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getInvoiceType() { return invoiceType; }
    public void setInvoiceType(String invoiceType) { this.invoiceType = invoiceType; }
    public String getTitleType() { return titleType; }
    public void setTitleType(String titleType) { this.titleType = titleType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTaxNo() { return taxNo; }
    public void setTaxNo(String taxNo) { this.taxNo = taxNo; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getInvoiceUrl() { return invoiceUrl; }
    public void setInvoiceUrl(String invoiceUrl) { this.invoiceUrl = invoiceUrl; }
    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }
    public LocalDateTime getIssueTime() { return issueTime; }
    public void setIssueTime(LocalDateTime issueTime) { this.issueTime = issueTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
