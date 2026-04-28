package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 产品表单字段
 */
@Entity
@Table(name = "t_product_field")
@Comment("产品表单字段表")
public class ProductField extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    @Comment("产品ID")
    private Long productId;

    @Column(name = "field_name", nullable = false, length = 64)
    @Comment("字段名称")
    private String fieldName;

    @Column(name = "field_key", nullable = false, length = 64)
    @Comment("字段标识")
    private String fieldKey;

    @Column(name = "field_type", length = 32)
    @Comment("字段类型 text/textarea/select/radio/checkbox/image/file")
    private String fieldType = "text";

    @Column(length = 256)
    @Comment("占位提示")
    private String placeholder;

    @Column(columnDefinition = "TEXT")
    @Comment("选项值(JSON数组)")
    private String options;

    @Column(name = "is_required")
    @Comment("是否必填 0-否 1-是")
    private Integer isRequired = 0;

    @Column(length = 128)
    @Comment("校验规则(正则)")
    private String validation;

    @Column(name = "error_msg", length = 128)
    @Comment("校验失败提示")
    private String errorMsg;

    @Comment("排序值")
    private Integer sort = 0;

    @Comment("状态 0-禁用 1-启用")
    private Integer status = 1;

    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getFieldKey() { return fieldKey; }
    public void setFieldKey(String fieldKey) { this.fieldKey = fieldKey; }
    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    public Integer getIsRequired() { return isRequired; }
    public void setIsRequired(Integer isRequired) { this.isRequired = isRequired; }
    public String getValidation() { return validation; }
    public void setValidation(String validation) { this.validation = validation; }
    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
