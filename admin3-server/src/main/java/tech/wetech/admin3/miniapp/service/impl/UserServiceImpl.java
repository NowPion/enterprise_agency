package tech.wetech.admin3.miniapp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.common.Constants;
import tech.wetech.admin3.common.SessionItemHolder;
import tech.wetech.admin3.common.authz.JwtUtils;
import tech.wetech.admin3.miniapp.dto.UpdateUserRequest;
import tech.wetech.admin3.miniapp.dto.WxLoginRequest;
import tech.wetech.admin3.miniapp.service.IUserService;
import tech.wetech.admin3.miniapp.vo.LoginVO;
import tech.wetech.admin3.miniapp.vo.UserInfoVO;
import tech.wetech.admin3.miniapp.vo.UserStatsVO;
import tech.wetech.admin3.sys.model.MiniappUser;
import tech.wetech.admin3.sys.repository.MiniappUserRepository;
import tech.wetech.admin3.sys.repository.OrderRepository;
import tech.wetech.admin3.wechat.WechatClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序用户服务实现
 */
@Service
public class UserServiceImpl implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final MiniappUserRepository userRepository;
    private final WechatClient wechatClient;
    private final JwtUtils jwtUtils;
    private final OrderRepository orderRepository;

    public UserServiceImpl(MiniappUserRepository userRepository,
                           WechatClient wechatClient,
                           JwtUtils jwtUtils,
                           OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.wechatClient = wechatClient;
        this.jwtUtils = jwtUtils;
        this.orderRepository = orderRepository;
    }

    /**
     * 开发模式固定的模拟openid
     */
    private static final String DEV_MOCK_OPENID = "dev_test_openid_001";

    @Override
    @Transactional
    public LoginVO wxLogin(WxLoginRequest request, String clientIp) {
        String openid;
        String unionid = null;
        String phoneNumber = null;
        boolean isDevMode = false;

        // 检查是否配置了微信小程序
        String appId = wechatClient.getAppId();
        if (appId == null || appId.isBlank()) {
            // 开发模式：未配置appId时使用固定的模拟用户
            openid = DEV_MOCK_OPENID;
            phoneNumber = "13800138000";
            isDevMode = true;
        } else {
            // 正式模式：调用微信接口
            WechatClient.WxSession session = wechatClient.code2Session(request.getCode());
            if (!session.isSuccess()) {
                throw new BusinessException(CommonResultStatus.FAIL, "登录失败: " + session.errmsg());
            }
            openid = session.openid();
            unionid = session.unionid();

            // 获取手机号
            if (request.getPhoneCode() != null && !request.getPhoneCode().isBlank()) {
                WechatClient.PhoneInfo phoneInfo = wechatClient.getPhoneNumber(request.getPhoneCode());
                if (phoneInfo.isSuccess()) {
                    phoneNumber = phoneInfo.phoneNumber();
                } else {
                    log.warn("获取手机号失败: {}", phoneInfo.errmsg());
                }
            }
        }

        MiniappUser user = userRepository.findByOpenid(openid).orElse(null);
        boolean isNewUser = (user == null);

        if (isNewUser) {
            // 新用户注册时检查手机号是否被拉黑
            if (phoneNumber != null && !phoneNumber.isBlank()) {
                if (userRepository.existsByPhoneAndBlocked(phoneNumber, 1)) {
                    throw new BusinessException(CommonResultStatus.FORBIDDEN, "该手机号已被限制使用");
                }
            }
            user = createNewUser(openid, unionid, phoneNumber, request, isDevMode);
        } else {
            // 已有用户检查是否被拉黑
            if (user.getBlocked() != null && user.getBlocked() == 1) {
                throw new BusinessException(CommonResultStatus.FORBIDDEN, "账号已被拉黑，无法登录");
            }
            updateExistingUser(user, unionid, phoneNumber, request);
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(CommonResultStatus.FORBIDDEN, "账号已被禁用");
        }

        user.setLastLoginTime(LocalDateTime.now());
        user = userRepository.save(user);

        String token = jwtUtils.generateToken(user.getId(), openid);

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserInfo(buildUserInfo(user, isNewUser));
        return vo;
    }

    @Override
    public UserInfoVO getUserInfo() {
        MiniappUser user = getCurrentUser();
        return buildFullUserInfo(user);
    }

    @Override
    @Transactional
    public void updateUserInfo(UpdateUserRequest request) {
        MiniappUser user = getCurrentUser();

        if (request.getNickName() != null) user.setNickname(request.getNickName());
        if (request.getAvatarUrl() != null) user.setAvatar(request.getAvatarUrl());
        if (request.getGender() != null) user.setGender(request.getGender());

        userRepository.save(user);
    }

    @Override
    public UserStatsVO getUserStats() {
        MiniappUser user = getCurrentUser();

        UserStatsVO vo = new UserStatsVO();
        vo.setUserId(user.getId());
        return vo;
    }

    @Override
    public Map<String, Object> getOrderStats() {
        MiniappUser user = getCurrentUser();
        Map<String, Object> stats = new HashMap<>();
        
        long pending = orderRepository.countByUserIdAndStatus(user.getId(), "pending");
        long paid = orderRepository.countByUserIdAndStatus(user.getId(), "paid");
        long processing = orderRepository.countByUserIdAndStatus(user.getId(), "processing");
        long completed = orderRepository.countByUserIdAndStatus(user.getId(), "completed");
        long total = orderRepository.countByUserId(user.getId());
        
        stats.put("pending", pending);
        stats.put("paid", paid);
        stats.put("processing", processing);
        stats.put("completed", completed);
        stats.put("total", total);
        return stats;
    }

    @Override
    public MiniappUser checkLogin() {
        return getCurrentUser();
    }

    @Override
    public MiniappUser getCurrentUser() {
        Object obj = SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
        if (obj instanceof MiniappUser user) {
            return user;
        }
        throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "请先登录");
    }

    @Override
    public void logout() {
        SessionItemHolder.removeItem(Constants.SESSION_CURRENT_USER);
    }

    // ========== 私有方法 ==========

    private MiniappUser createNewUser(String openid, String unionid, String phoneNumber, WxLoginRequest request, boolean isDevMode) {
        MiniappUser user = new MiniappUser();
        user.setOpenid(openid);
        user.setUnionid(unionid);
        user.setPhone(phoneNumber);

        if (isDevMode) {
            user.setNickname("测试用户");
            user.setAvatar("");
        } else {
            user.setNickname(request.getNickName() != null ? request.getNickName() : "微信用户");
            user.setAvatar(request.getAvatarUrl() != null ? request.getAvatarUrl() : "");
        }

        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        return user;
    }

    private void updateExistingUser(MiniappUser user, String unionid, String phoneNumber, WxLoginRequest request) {
        if (request.getNickName() != null) user.setNickname(request.getNickName());
        if (request.getAvatarUrl() != null) user.setAvatar(request.getAvatarUrl());
        if (unionid != null) user.setUnionid(unionid);
        if (phoneNumber != null) user.setPhone(phoneNumber);
    }

    private UserInfoVO buildUserInfo(MiniappUser user, boolean isNewUser) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setOpenid(user.getOpenid());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setIsNewUser(isNewUser);
        return vo;
    }

    private UserInfoVO buildFullUserInfo(MiniappUser user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setOpenid(user.getOpenid());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setGender(user.getGender());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return "";
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
