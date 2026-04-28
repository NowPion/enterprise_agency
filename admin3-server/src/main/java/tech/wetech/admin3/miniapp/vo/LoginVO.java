package tech.wetech.admin3.miniapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 登录返回VO
 */
@Schema(description = "登录结果")
public class LoginVO {

    @Schema(description = "用户登录凭证，后续请求需携带")
    private String token;

    @Schema(description = "用户信息")
    private UserInfoVO userInfo;

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UserInfoVO getUserInfo() { return userInfo; }
    public void setUserInfo(UserInfoVO userInfo) { this.userInfo = userInfo; }
}
