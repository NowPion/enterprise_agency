package tech.wetech.admin3.miniapp.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单VO
 */
public class OrderVO {
    private Long id;
    private String orderNo;
    private String productName;
    private String requiredMaterials; // 所需材料说明
    private BigDecimal amount;
    private BigDecimal discountAmount; // 优惠金额
    private BigDecimal payAmount; // 实付金额
    private Integer status;
    private String statusText;
    private String formData;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime completeTime;
    private List<ProgressVO> progressList;
    private List<MaterialVO> materialList;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getRequiredMaterials() { return requiredMaterials; }
    public void setRequiredMaterials(String requiredMaterials) { this.requiredMaterials = requiredMaterials; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getPayAmount() { return payAmount; }
    public void setPayAmount(BigDecimal payAmount) { this.payAmount = payAmount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }
    public String getFormData() { return formData; }
    public void setFormData(String formData) { this.formData = formData; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getPayTime() { return payTime; }
    public void setPayTime(LocalDateTime payTime) { this.payTime = payTime; }
    public LocalDateTime getCompleteTime() { return completeTime; }
    public void setCompleteTime(LocalDateTime completeTime) { this.completeTime = completeTime; }
    public List<ProgressVO> getProgressList() { return progressList; }
    public void setProgressList(List<ProgressVO> progressList) { this.progressList = progressList; }
    public List<MaterialVO> getMaterialList() { return materialList; }
    public void setMaterialList(List<MaterialVO> materialList) { this.materialList = materialList; }

    public static class ProgressVO {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createTime;
        private List<MaterialVO> materials; // 该进度关联的材料（办理人员上传）

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
        public List<MaterialVO> getMaterials() { return materials; }
        public void setMaterials(List<MaterialVO> materials) { this.materials = materials; }
    }

    public static class MaterialVO {
        private Long id;
        private String name;
        private String fileUrl;
        private LocalDateTime uploadTime;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getFileUrl() { return fileUrl; }
        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
        public LocalDateTime getUploadTime() { return uploadTime; }
        public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }
    }
}
