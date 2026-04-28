package tech.wetech.admin3.miniapp.dto;

/**
 * 申请发票DTO
 */
public class ApplyInvoiceDTO {
    
    private Long orderId;
    private Integer type; // 1-普通发票 2-增值税专用发票
    private Integer titleType; // 1-个人 2-企业
    private String title; // 发票抬头
    private String taxNo; // 税号（企业必填）
    private String email; // 接收邮箱
    
    // 增值税专用发票额外信息
    private String bankName; // 开户银行
    private String bankAccount; // 银行账号
    private String address; // 企业地址
    private String phone; // 企业电话

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public Integer getTitleType() { return titleType; }
    public void setTitleType(Integer titleType) { this.titleType = titleType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTaxNo() { return taxNo; }
    public void setTaxNo(String taxNo) { this.taxNo = taxNo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
