package tech.wetech.admin3.miniapp.auth;

import java.lang.annotation.*;

/**
 * 小程序用户认证注解
 * 用于Controller方法参数，自动注入当前登录用户ID
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiniappAuth {
}
