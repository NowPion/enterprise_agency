package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 企业信息缓存表
 */
@Entity
@Table(name = "t_enterprise_info", indexes = {
    @Index(name = "idx_company_name", columnList = "company_name"),
    @Index(name = "idx_credit_no", columnList = "credit_no")
})
@Comment("企业信息缓存表")
public class EnterpriseInfo extends BaseEntity {

    @Column(name = "company_name", nullable = false, length = 256)
    @Comment("公司名称")
    private String companyName;

    @Column(name = "credit_no", length = 64)
    @Comment("统一社会信用代码")
    private String creditNo;

    @Column(name = "company_code", length = 64)
    @Comment("注册号")
    private String companyCode;

    @Column(name = "legal_person", length = 64)
    @Comment("法人")
    private String legalPerson;

    @Column(name = "company_status", length = 32)
    @Comment("登记状态")
    private String companyStatus;

    @Column(name = "establish_date", length = 16)
    @Comment("成立时间")
    private String establishDate;

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
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCreditNo() { return creditNo; }
    public void setCreditNo(String creditNo) { this.creditNo = creditNo; }
    public String getCompanyCode() { return companyCode; }
    public void setCompanyCode(String companyCode) { this.companyCode = companyCode; }
    public String getLegalPerson() { return legalPerson; }
    public void setLegalPerson(String legalPerson) { this.legalPerson = legalPerson; }
    public String getCompanyStatus() { return companyStatus; }
    public void setCompanyStatus(String companyStatus) { this.companyStatus = companyStatus; }
    public String getEstablishDate() { return establishDate; }
    public void setEstablishDate(String establishDate) { this.establishDate = establishDate; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
