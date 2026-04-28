package tech.wetech.admin3.miniapp.vo;

/**
 * 版本检查VO
 */
public class VersionVO {
    private Boolean hasUpdate;
    private String version;
    private String content;
    private Boolean forceUpdate;
    private String downloadUrl;

    public VersionVO() {}

    public static VersionVO noUpdate(String currentVersion) {
        VersionVO vo = new VersionVO();
        vo.setHasUpdate(false);
        vo.setVersion(currentVersion);
        vo.setContent("");
        vo.setForceUpdate(false);
        vo.setDownloadUrl("");
        return vo;
    }

    public Boolean getHasUpdate() { return hasUpdate; }
    public void setHasUpdate(Boolean hasUpdate) { this.hasUpdate = hasUpdate; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Boolean getForceUpdate() { return forceUpdate; }
    public void setForceUpdate(Boolean forceUpdate) { this.forceUpdate = forceUpdate; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}
