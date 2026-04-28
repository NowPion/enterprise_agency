package tech.wetech.admin3.miniapp.vo;

/**
 * 上传结果VO
 */
public class UploadVO {
    private String url;
    private String path;
    private Long size;

    public UploadVO() {}

    public UploadVO(String url, String path, Long size) {
        this.url = url;
        this.path = path;
        this.size = size;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }
}
