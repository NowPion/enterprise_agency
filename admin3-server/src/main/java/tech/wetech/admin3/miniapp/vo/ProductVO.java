package tech.wetech.admin3.miniapp.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品VO
 */
public class ProductVO {
    private Long id;
    private String name;
    private String code;
    private String category;
    private String image;
    private String description;
    private BigDecimal originalPrice;
    private BigDecimal price;
    private Integer processDays;
    private String notice;
    private String materials;
    private List<ProductFieldVO> fields;
    private List<YearPriceVO> yearPrices;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getProcessDays() { return processDays; }
    public void setProcessDays(Integer processDays) { this.processDays = processDays; }
    public String getNotice() { return notice; }
    public void setNotice(String notice) { this.notice = notice; }
    public String getMaterials() { return materials; }
    public void setMaterials(String materials) { this.materials = materials; }
    public List<ProductFieldVO> getFields() { return fields; }
    public void setFields(List<ProductFieldVO> fields) { this.fields = fields; }
    public List<YearPriceVO> getYearPrices() { return yearPrices; }
    public void setYearPrices(List<YearPriceVO> yearPrices) { this.yearPrices = yearPrices; }

    public static class ProductFieldVO {
        private Long id;
        private String fieldName;
        private String fieldKey;
        private String fieldType;
        private String placeholder;
        private Boolean isRequired;
        private Integer sort;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        public String getFieldKey() { return fieldKey; }
        public void setFieldKey(String fieldKey) { this.fieldKey = fieldKey; }
        public String getFieldType() { return fieldType; }
        public void setFieldType(String fieldType) { this.fieldType = fieldType; }
        public String getPlaceholder() { return placeholder; }
        public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }
        public Boolean getIsRequired() { return isRequired; }
        public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }
        public Integer getSort() { return sort; }
        public void setSort(Integer sort) { this.sort = sort; }
    }

    public static class YearPriceVO {
        private Long id;
        private String year;
        private BigDecimal originalPrice;
        private BigDecimal price;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getYear() { return year; }
        public void setYear(String year) { this.year = year; }
        public BigDecimal getOriginalPrice() { return originalPrice; }
        public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }
}
