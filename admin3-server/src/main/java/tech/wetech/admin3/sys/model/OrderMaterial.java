package tech.wetech.admin3.sys.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 订单材料
 */
@Entity
@Table(name = "t_order_material")
@Comment("订单材料表")
public class OrderMaterial extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    @Comment("订单ID")
    private Long orderId;

    @Column(name = "order_no", length = 32)
    @Comment("订单号")
    private String orderNo;

    @Column(name = "progress_id")
    @Comment("关联进度ID")
    private Long progressId;

    @Column(nullable = false, length = 64)
    @Comment("材料名称")
    private String name;

    @Column(name = "material_type", length = 32)
    @Comment("材料类型 license-营业执照 report-年报截图 id_card-身份证 other-其他")
    private String materialType;

    @Column(name = "file_url", nullable = false, length = 512)
    @Comment("文件URL")
    private String fileUrl;

    @Column(name = "file_type", length = 32)
    @Comment("文件类型 image/pdf/doc")
    private String fileType;

    @Column(name = "file_size")
    @Comment("文件大小(字节)")
    private Long fileSize;

    @Column(name = "upload_type", length = 16)
    @Comment("上传类型 user-用户上传 admin-后台上传")
    private String uploadType = "admin";

    @Column(name = "uploader_id")
    @Comment("上传人ID")
    private Long uploaderId;

    @Column(name = "uploader_name", length = 32)
    @Comment("上传人姓名")
    private String uploaderName;

    @Column(length = 256)
    @Comment("备注")
    private String remark;

    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getProgressId() { return progressId; }
    public void setProgressId(Long progressId) { this.progressId = progressId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getUploadType() { return uploadType; }
    public void setUploadType(String uploadType) { this.uploadType = uploadType; }
    public Long getUploaderId() { return uploaderId; }
    public void setUploaderId(Long uploaderId) { this.uploaderId = uploaderId; }
    public String getUploaderName() { return uploaderName; }
    public void setUploaderName(String uploaderName) { this.uploaderName = uploaderName; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
