package tech.wetech.admin3.miniapp.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 小程序 JWT 配置
 */
@Component
@ConfigurationProperties(prefix = "miniapp.jwt")
public class MiniappJwtProperties {

    /**
     * JWT 密钥
     */
    private String secret = "tongue-diagnosis-miniapp-jwt-secret-key-2025";

    /**
     * Token 有效期（毫秒），默认7天
     */
    private long expiration = 7 * 24 * 60 * 60 * 1000L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}
