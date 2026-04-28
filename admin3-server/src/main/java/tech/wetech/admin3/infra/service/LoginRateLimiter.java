package tech.wetech.admin3.infra.service;

import org.springframework.stereotype.Component;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录限流器
 * 防止暴力破解密码
 */
@Component
public class LoginRateLimiter {

    // 最大失败次数
    private static final int MAX_ATTEMPTS = 5;
    // 锁定时间（分钟）
    private static final int LOCK_MINUTES = 15;
    // 失败记录过期时间（分钟）
    private static final int ATTEMPT_EXPIRE_MINUTES = 30;

    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    /**
     * 检查是否被锁定
     */
    public void checkLocked(String key) {
        LoginAttempt attempt = attempts.get(key);
        if (attempt != null && attempt.isLocked()) {
            long remainingMinutes = attempt.getRemainingLockMinutes();
            throw new BusinessException(CommonResultStatus.FAIL, 
                "登录失败次数过多，请" + remainingMinutes + "分钟后再试");
        }
    }

    /**
     * 记录登录失败
     */
    public void recordFailure(String key) {
        attempts.compute(key, (k, attempt) -> {
            if (attempt == null || attempt.isExpired()) {
                attempt = new LoginAttempt();
            }
            attempt.incrementFailures();
            return attempt;
        });
    }

    /**
     * 登录成功，清除失败记录
     */
    public void recordSuccess(String key) {
        attempts.remove(key);
    }

    /**
     * 获取剩余尝试次数
     */
    public int getRemainingAttempts(String key) {
        LoginAttempt attempt = attempts.get(key);
        if (attempt == null || attempt.isExpired()) {
            return MAX_ATTEMPTS;
        }
        return Math.max(0, MAX_ATTEMPTS - attempt.getFailures());
    }

    private static class LoginAttempt {
        private int failures = 0;
        private LocalDateTime firstFailureTime = LocalDateTime.now();
        private LocalDateTime lockTime = null;

        void incrementFailures() {
            failures++;
            if (failures >= MAX_ATTEMPTS && lockTime == null) {
                lockTime = LocalDateTime.now();
            }
        }

        boolean isLocked() {
            if (lockTime == null) return false;
            return LocalDateTime.now().isBefore(lockTime.plusMinutes(LOCK_MINUTES));
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(firstFailureTime.plusMinutes(ATTEMPT_EXPIRE_MINUTES));
        }

        int getFailures() {
            return failures;
        }

        long getRemainingLockMinutes() {
            if (lockTime == null) return 0;
            LocalDateTime unlockTime = lockTime.plusMinutes(LOCK_MINUTES);
            long minutes = java.time.Duration.between(LocalDateTime.now(), unlockTime).toMinutes();
            return Math.max(1, minutes + 1);
        }
    }
}
