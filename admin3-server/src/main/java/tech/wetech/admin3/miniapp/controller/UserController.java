package tech.wetech.admin3.miniapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.miniapp.ApiResult;
import tech.wetech.admin3.miniapp.dto.UpdateUserRequest;
import tech.wetech.admin3.miniapp.dto.WxLoginRequest;
import tech.wetech.admin3.miniapp.service.IUserService;
import tech.wetech.admin3.miniapp.vo.LoginVO;
import tech.wetech.admin3.miniapp.vo.UserInfoVO;
import tech.wetech.admin3.miniapp.vo.UserStatsVO;
import tech.wetech.admin3.sys.model.MiniappUser;

import java.util.HashMap;
import java.util.Map;

/**
 * 小程序用户接口
 */
@Tag(name = "小程序-用户", description = "小程序用户相关接口")
@RestController("miniappApiUserController")
@RequestMapping("/minapp/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "微信登录")
    @PostMapping("/wxLogin")
    public ApiResult<LoginVO> wxLogin(@RequestBody WxLoginRequest request, HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);
        return ApiResult.ok("登录成功", userService.wxLogin(request, clientIp));
    }

    /**
     * 获取客户端真实IP（支持代理场景）
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For可能包含多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        // // IPv6本地回环地址转换为IPv4格式
        // if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
        //     ip = "127.0.0.1";
        // }
        return ip;
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public ApiResult<UserInfoVO> getUserInfo() {
        return ApiResult.ok(userService.getUserInfo());
    }

    @Operation(summary = "更新用户信息")
    @PostMapping("/update")
    public ApiResult<Void> updateUserInfo(@RequestBody UpdateUserRequest request) {
        userService.updateUserInfo(request);
        return ApiResult.ok("更新成功");
    }

    @Operation(summary = "获取用户统计数据")
    @GetMapping("/stats")
    public ApiResult<UserStatsVO> getUserStats() {
        return ApiResult.ok(userService.getUserStats());
    }

    @Operation(summary = "获取订单统计")
    @GetMapping("/order-stats")
    public ApiResult<Map<String, Object>> getOrderStats() {
        return ApiResult.ok(userService.getOrderStats());
    }

    @Operation(summary = "检查登录状态")
    @GetMapping("/checkLogin")
    public ApiResult<Map<String, Object>> checkLogin() {
        MiniappUser user = userService.checkLogin();
        Map<String, Object> data = new HashMap<>();
        data.put("isLogin", true);
        data.put("userId", user.getId());
        return ApiResult.ok(data);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public ApiResult<Void> logout() {
        userService.logout();
        return ApiResult.ok("退出成功");
    }
}
