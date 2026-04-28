package tech.wetech.admin3.miniapp.dto;

/**
 * 上传材料DTO
 */
public class UploadMaterialDTO {
    private String name;           // 材料名称
    private String materialType;   // 材料类型 license-营业执照 report-年报截图 id_card-身份证 other-其他
    private String fileUrl;        // 文件URL
    private String fileType;       // 文件类型 image/pdf/doc
    private Long fileSize;         // 文件大小(字节)
    private String remark;         // 备注

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
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
