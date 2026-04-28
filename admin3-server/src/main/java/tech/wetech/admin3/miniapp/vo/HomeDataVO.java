package tech.wetech.admin3.miniapp.vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 首页数据VO
 */
public class HomeDataVO {
    
    private List<BannerVO> banners;
    private List<ProductVO> hotProducts;
    private List<ExampleProgressVO> exampleProgress;
    private List<ContentVO> notices;

    // Getters and Setters
    public List<BannerVO> getBanners() { return banners; }
    public void setBanners(List<BannerVO> banners) { this.banners = banners; }
    public List<ProductVO> getHotProducts() { return hotProducts; }
    public void setHotProducts(List<ProductVO> hotProducts) { this.hotProducts = hotProducts; }
    public List<ExampleProgressVO> getExampleProgress() { return exampleProgress; }
    public void setExampleProgress(List<ExampleProgressVO> exampleProgress) { this.exampleProgress = exampleProgress; }
    public List<ContentVO> getNotices() { return notices; }
    public void setNotices(List<ContentVO> notices) { this.notices = notices; }

    /**
     * 示例进度VO（展示其他用户加急办理的示例数据）
     */
    public static class ExampleProgressVO {
        private String companyName;
        private String businessType;
        private String status;
        private String statusText;
        private LocalDateTime createTime;

        // Getters and Setters
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public String getBusinessType() { return businessType; }
        public void setBusinessType(String businessType) { this.businessType = businessType; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getStatusText() { return statusText; }
        public void setStatusText(String statusText) { this.statusText = statusText; }
        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    }
}
