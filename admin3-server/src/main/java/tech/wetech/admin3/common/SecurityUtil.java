package tech.wetech.admin3.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 安全工具类
 * @author cjbi
 */
public class SecurityUtil {

    private static final BCryptPasswordEncoder BCRYPT_ENCODER = new BCryptPasswordEncoder();

    /**
     * BCrypt 加密密码
     */
    public static String encryptPassword(String rawPassword) {
        return BCRYPT_ENCODER.encode(rawPassword);
    }

    /**
     * BCrypt 验证密码
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return BCRYPT_ENCODER.matches(rawPassword, encodedPassword);
    }

    /**
     * 判断是否为 BCrypt 格式的密码
     */
    public static boolean isBCryptPassword(String password) {
        return password != null && password.startsWith("$2");
    }

    /**
     * @deprecated 使用 BCrypt 替代，此方法仅用于兼容旧数据
     */
    @Deprecated
    public static String md5(String username, String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(username.getBytes());
        md.update(password.getBytes());
        return new BigInteger(1, md.digest()).toString(16);
    }
}
