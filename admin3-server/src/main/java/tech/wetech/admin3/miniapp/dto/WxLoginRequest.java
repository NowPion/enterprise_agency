package tech.wetech.admin3.miniapp.dto;

/**
 * 微信登录请求
 */
public class WxLoginRequest {
    private String code;
    private String phoneCode;
    private String nickName;
    private String avatarUrl;
    private String inviter;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getPhoneCode() { return phoneCode; }
    public void setPhoneCode(String phoneCode) { this.phoneCode = phoneCode; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getInviter() { return inviter; }
    public void setInviter(String inviter) { this.inviter = inviter; }
}
