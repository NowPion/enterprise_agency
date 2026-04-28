package tech.wetech.admin3.infra;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import tech.wetech.admin3.common.EventStore;
import tech.wetech.admin3.common.authz.JwtUtils;
import tech.wetech.admin3.miniapp.auth.MiniappAuthArgumentResolver;
import tech.wetech.admin3.miniapp.auth.MiniappAuthInterceptor;
import tech.wetech.admin3.sys.repository.MiniappUserRepository;
import tech.wetech.admin3.sys.service.SessionService;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author cjbi
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

  private final SessionService sessionService;
  private final EventStore eventStore;
  private final MiniappUserRepository miniappUserRepository;
  private final JwtUtils jwtUtils;
  private final MiniappAuthArgumentResolver miniappAuthArgumentResolver;

  public WebMvcConfiguration(SessionService sessionService, EventStore eventStore,
                             MiniappUserRepository miniappUserRepository, JwtUtils jwtUtils,
                             MiniappAuthArgumentResolver miniappAuthArgumentResolver) {
    this.sessionService = sessionService;
    this.eventStore = eventStore;
    this.miniappUserRepository = miniappUserRepository;
    this.jwtUtils = jwtUtils;
    this.miniappAuthArgumentResolver = miniappAuthArgumentResolver;
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOriginPatterns("*")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .exposedHeaders("Authorization")
        .allowCredentials(true)
        .maxAge(3600);
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(miniappAuthArgumentResolver);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 管理端认证拦截器
    InterceptorRegistration loginInterceptor = registry.addInterceptor(new AuthInterceptor(sessionService));
    loginInterceptor.addPathPatterns("/admin/**");
    loginInterceptor.excludePathPatterns(
      "/admin/login",         // 登录接口不需要认证
      "/admin/storage/fetch/**",
      "/admin/storage/download/**",
      "/swagger-ui.html",
      "/swagger-ui/**",
      "/v3/api-docs/**",
      "/assets/**",
      "/favicon.ico",
      "/avatar.jpg",
      "/index.html",
      "/",
      "/**/*.jpg",
      "/**/*.jpeg",
      "/**/*.png",
      "/**/*.gif",
      "/**/*.webp",
      "/**/*.svg",
      "/**/*.ico"
    );

    // 小程序认证拦截器
    InterceptorRegistration miniappInterceptor = registry.addInterceptor(new MiniappAuthInterceptor(miniappUserRepository, jwtUtils));
    miniappInterceptor.addPathPatterns("/minapp/**");
    miniappInterceptor.excludePathPatterns(
      "/minapp/user/wxLogin",     // 微信登录接口不需要认证
      "/minapp/home/**",          // 首页数据公开
      "/minapp/common/config",    // 系统配置公开
      "/minapp/common/agreement/**", // 协议公开
      "/minapp/product/**",       // 产品接口公开
      "/minapp/content/**",       // 内容接口公开
      "/minapp/wechat/pay/notify", // 支付回调不需要认证
      "/minapp/wechat/pay/refund-notify" // 退款回调不需要认证
    );

    InterceptorRegistration eventSubscribesInterceptor = registry.addInterceptor(new EventSubscribesInterceptor(eventStore, sessionService));
    eventSubscribesInterceptor.addPathPatterns("/admin/**");
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("/index.html");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/webjars/admin3-ui/");
  }

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder -> {
      //jpa entity serializers
      builder.serializers(BaseEntitySerializer.instance);

      //datetime formatter
      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

      //datetime deserializers
      builder.deserializers(new LocalDateDeserializer(dateFormatter));
      builder.deserializers(new LocalDateTimeDeserializer(dateTimeFormatter));

      //datetime serializers
      builder.serializers(new LocalDateSerializer(dateFormatter));
      builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));
    };
  }

}
