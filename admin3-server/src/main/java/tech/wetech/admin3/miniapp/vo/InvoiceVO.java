package tech.wetech.admin3.miniapp.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 发票VO
 */
public class InvoiceVO {
    
    private Long id;
    private String invoiceNo;
    private Long orderId;
    private String orderNo;
    private Integer type; // 1-普通发票 2-增值税专用发票
    private String typeText;
    private Integer titleType; // 1-个人 2-企业
    private String title;
    private String taxNo;
    private BigDecimal amount;
    private String status; // pending-待开票 issued-已开票 cancelled-已作废
    private String statusText;
    private String invoiceUrl; // 电子发票URL
    private LocalDateTime createTime;
    private LocalDateTime issueTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public String getTypeText() { return typeText; }
    public void setTypeText(String typeText) { this.typeText = typeText; }
    public Integer getTitleType() { return titleType; }
    public void setTitleType(Integer titleType) { this.titleType = titleType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTaxNo() { return taxNo; }
    public void setTaxNo(String taxNo) { this.taxNo = taxNo; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }
    public String getInvoiceUrl() { return invoiceUrl; }
    public void setInvoiceUrl(String invoiceUrl) { this.invoiceUrl = invoiceUrl; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getIssueTime() { return issueTime; }
    public void setIssueTime(LocalDateTime issueTime) { this.issueTime = issueTime; }
}
