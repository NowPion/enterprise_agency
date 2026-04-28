package tech.wetech.admin3.miniapp.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.common.Constants;
import tech.wetech.admin3.common.SessionItemHolder;
import tech.wetech.admin3.common.authz.JwtUtils;
import tech.wetech.admin3.sys.model.MiniappUser;
import tech.wetech.admin3.sys.repository.MiniappUserRepository;

/**
 * 小程序 JWT 认证拦截器
 */
public class MiniappAuthInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MiniappUserRepository miniappUserRepository;
    private final JwtUtils jwtUtils;

    public MiniappAuthInterceptor(MiniappUserRepository miniappUserRepository, JwtUtils jwtUtils) {
        this.miniappUserRepository = miniappUserRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取 token
        String token = extractToken(request);
        
        log.info("Miniapp auth interceptor - URI: {}, Authorization header: {}", 
            request.getRequestURI(), 
            request.getHeader("Authorization") != null ? "present" : "missing");

        if (token == null || token.isEmpty()) {
            log.warn("Miniapp request {} {} missing token", request.getMethod(), request.getRequestURI());
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "请先登录");
        }

        try {
            // 解析 JWT 获取用户ID
            Long userId = jwtUtils.getUserId(token);
            log.info("Miniapp auth - parsed userId from token: {}", userId);

            // 查询用户
            MiniappUser user = miniappUserRepository.findById(userId).orElse(null);
            log.info("Miniapp auth - user found: {}", user != null);
            
            if (user == null) {
                log.warn("Miniapp auth - user not found for userId: {}", userId);
                throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "用户不存在");
            }

            if (user.getStatus() != 1) {
                log.warn("Miniapp auth - user disabled, status: {}", user.getStatus());
                throw new BusinessException(CommonResultStatus.FORBIDDEN, "账号已被禁用");
            }

            // 存储当前用户信息
            SessionItemHolder.setItem(Constants.SESSION_CURRENT_USER, user);
            // 同时存储到request attribute，供MiniappAuthArgumentResolver使用
            request.setAttribute("userId", user.getId());
            log.info("Miniapp auth - user authenticated successfully: {}", userId);
            return true;

        } catch (ExpiredJwtException e) {
            log.warn("Token expired for request: {}", request.getRequestURI());
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "登录已过期，请重新登录");
        } catch (JwtException e) {
            log.warn("Invalid token for request: {}, error: {}", request.getRequestURI(), e.getMessage());
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无效的登录凭证");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Miniapp auth - unexpected error: {}", e.getMessage(), e);
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "请先登录");
        }
    }

    /**
     * 从请求中提取 Token
     */
    private String extractToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return request.getHeader("X-User-Token");
    }
}
