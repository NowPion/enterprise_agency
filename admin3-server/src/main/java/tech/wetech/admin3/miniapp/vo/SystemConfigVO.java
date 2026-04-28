package tech.wetech.admin3.miniapp.vo;

/**
 * 系统配置VO
 */
public class SystemConfigVO {
    private String appName;
    private String version;
    private String servicePhone;
    private String serviceWechat;
    private String serviceWorkTime;
    private Integer registerReward;
    private Integer inviteReward;
    private Integer adReward;

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getServicePhone() { return servicePhone; }
    public void setServicePhone(String servicePhone) { this.servicePhone = servicePhone; }
    public String getServiceWechat() { return serviceWechat; }
    public void setServiceWechat(String serviceWechat) { this.serviceWechat = serviceWechat; }
    public String getServiceWorkTime() { return serviceWorkTime; }
    public void setServiceWorkTime(String serviceWorkTime) { this.serviceWorkTime = serviceWorkTime; }
    public Integer getRegisterReward() { return registerReward; }
    public void setRegisterReward(Integer registerReward) { this.registerReward = registerReward; }
    public Integer getInviteReward() { return inviteReward; }
    public void setInviteReward(Integer inviteReward) { this.inviteReward = inviteReward; }
    public Integer getAdReward() { return adReward; }
    public void setAdReward(Integer adReward) { this.adReward = adReward; }
}
