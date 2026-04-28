package tech.wetech.admin3.miniapp.vo;

/**
 * 轮播图VO
 */
public class BannerVO {
    
    private Long id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private Integer linkType; // 1-页面跳转 2-外部链接 3-产品详情

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }
    public Integer getLinkType() { return linkType; }
    public void setLinkType(Integer linkType) { this.linkType = linkType; }
}
