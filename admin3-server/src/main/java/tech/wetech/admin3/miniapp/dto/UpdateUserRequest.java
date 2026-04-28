package tech.wetech.admin3.miniapp.dto;

/**
 * 更新用户信息请求
 */
public class UpdateUserRequest {
    private String nickName;
    private String avatarUrl;
    private Integer gender;

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
}
