package tech.wetech.admin3.miniapp.service;

import tech.wetech.admin3.miniapp.dto.UpdateUserRequest;
import tech.wetech.admin3.miniapp.dto.WxLoginRequest;
import tech.wetech.admin3.miniapp.vo.LoginVO;
import tech.wetech.admin3.miniapp.vo.UserInfoVO;
import tech.wetech.admin3.miniapp.vo.UserStatsVO;
import tech.wetech.admin3.sys.model.MiniappUser;

/**
 * 小程序用户服务接口
 */
public interface IUserService {

    /**
     * 微信登录
     * @param request 登录请求
     * @param clientIp 客户端IP
     */
    LoginVO wxLogin(WxLoginRequest request, String clientIp);

    /**
     * 获取当前用户信息
     */
    UserInfoVO getUserInfo();

    /**
     * 更新用户信息
     */
    void updateUserInfo(UpdateUserRequest request);

    /**
     * 获取用户统计数据
     */
    UserStatsVO getUserStats();

    /**
     * 获取订单统计
     */
    java.util.Map<String, Object> getOrderStats();

    /**
     * 检查登录状态
     */
    MiniappUser checkLogin();

    /**
     * 获取当前登录用户
     */
    MiniappUser getCurrentUser();

    /**
     * 退出登录
     */
    void logout();
}
