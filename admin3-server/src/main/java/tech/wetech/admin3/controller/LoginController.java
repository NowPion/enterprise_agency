package tech.wetech.admin3.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.wetech.admin3.infra.service.LoginRateLimiter;
import tech.wetech.admin3.sys.service.SessionService;
import tech.wetech.admin3.sys.service.dto.UserinfoDTO;

/**
 * @author cjbi
 */
@RestController
@RequestMapping("/admin")
public class LoginController {

  private final SessionService sessionService;
  private final LoginRateLimiter loginRateLimiter;

  public LoginController(SessionService sessionService, LoginRateLimiter loginRateLimiter) {
    this.sessionService = sessionService;
    this.loginRateLimiter = loginRateLimiter;
  }

  @PostMapping("/login")
  private ResponseEntity<UserinfoDTO> login(@RequestBody @Valid LoginRequest request, HttpServletRequest httpRequest) {
    String clientKey = getClientKey(httpRequest, request.username());
    // 检查是否被锁定
    loginRateLimiter.checkLocked(clientKey);
    try {
      UserinfoDTO userinfo = sessionService.login(request.username(), request.password());
      // 登录成功，清除失败记录
      loginRateLimiter.recordSuccess(clientKey);
      return ResponseEntity.ok(userinfo);
    } catch (Exception e) {
      // 登录失败，记录失败次数
      loginRateLimiter.recordFailure(clientKey);
      int remaining = loginRateLimiter.getRemainingAttempts(clientKey);
      if (remaining > 0) {
        throw new tech.wetech.admin3.common.BusinessException(
            tech.wetech.admin3.common.CommonResultStatus.UNAUTHORIZED, 
            "密码不正确，还剩" + remaining + "次尝试机会");
      }
      throw e;
    }
  }

  private String getClientKey(HttpServletRequest request, String username) {
    String ip = getClientIP(request);
    return ip + ":" + username;
  }

  private String getClientIP(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    // 多个代理时取第一个IP
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0].trim();
    }
    return ip;
  }

  @SecurityRequirement(name = "bearerAuth")
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request) {
    String token = request.getHeader("Authorization").replace("Bearer", "").trim();
    sessionService.logout(token);
    return ResponseEntity.ok().build();
  }

  @SecurityRequirement(name = "bearerAuth")
  @GetMapping("/userinfo")
  public ResponseEntity<UserinfoDTO> userInfo(HttpServletRequest request) {
    String token = request.getHeader("Authorization").replace("Bearer", "").trim();
    return ResponseEntity.ok(sessionService.getLoginUserInfo(token));
  }

  record LoginRequest(@NotBlank String username, @NotBlank String password) {
  }


}
