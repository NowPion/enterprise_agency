package tech.wetech.admin3.wechat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tech.wetech.admin3.sys.service.SystemConfigService;

/**
 * 微信API客户端
 */
@Component
public class WechatClient {

    private static final Logger log = LoggerFactory.getLogger(WechatClient.class);
    private static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
    private static final String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    private static final String GET_PHONE_NUMBER_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=%s";

    // 配置键常量
    public static final String KEY_MINIAPP_APP_ID = "wechat.miniapp.appId";
    public static final String KEY_MINIAPP_APP_SECRET = "wechat.miniapp.appSecret";
    public static final String KEY_PAY_MCH_ID = "wechat.pay.mchId";
    public static final String KEY_PAY_MCH_KEY = "wechat.pay.mchKey";
    public static final String KEY_PAY_API_V3_KEY = "wechat.pay.apiV3Key";
    public static final String KEY_PAY_CERT_SERIAL_NO = "wechat.pay.certSerialNo";
    public static final String KEY_PAY_PRIVATE_KEY = "wechat.pay.privateKey";
    public static final String KEY_PAY_PRIVATE_CERT = "wechat.pay.privateCert";
    public static final String KEY_PAY_NOTIFY_URL = "wechat.pay.notifyUrl";
    public static final String KEY_PAY_REFUND_NOTIFY_URL = "wechat.pay.refundNotifyUrl";

    private final SystemConfigService configService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WechatClient(SystemConfigService configService) {
        this.configService = configService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 获取小程序AppID
     */
    public String getAppId() {
        return configService.getValue(KEY_MINIAPP_APP_ID, "");
    }

    /**
     * 获取小程序AppSecret
     */
    public String getAppSecret() {
        return configService.getValue(KEY_MINIAPP_APP_SECRET, "");
    }

    /**
     * 获取商户号
     */
    public String getMchId() {
        return configService.getValue(KEY_PAY_MCH_ID, "");
    }

    /**
     * 获取商户密钥
     */
    public String getMchKey() {
        return configService.getValue(KEY_PAY_MCH_KEY, "");
    }

    /**
     * 获取APIv3密钥
     */
    public String getApiV3Key() {
        return configService.getValue(KEY_PAY_API_V3_KEY, "");
    }

    /**
     * 通过code获取openid和session_key
     */
    public WxSession code2Session(String code) {
        String appId = getAppId();
        String appSecret = getAppSecret();

        if (appId.isEmpty() || appSecret.isEmpty()) {
            log.error("微信小程序配置未设置，请在系统配置中配置 wechat.miniapp.appId 和 wechat.miniapp.appSecret");
            return new WxSession(null, null, null, -1, "微信小程序配置未设置");
        }

        String url = String.format(CODE2SESSION_URL, appId, appSecret, code);

        try {
            String response = restTemplate.getForObject(url, String.class);
            log.debug("code2session response: {}", response);

            JsonNode json = objectMapper.readTree(response);
            
            if (json.has("errcode") && json.get("errcode").asInt() != 0) {
                int errcode = json.get("errcode").asInt();
                String errmsg = json.has("errmsg") ? json.get("errmsg").asText() : "未知错误";
                log.error("code2session failed: {} - {}", errcode, errmsg);
                return new WxSession(null, null, null, errcode, errmsg);
            }

            String openid = json.get("openid").asText();
            String sessionKey = json.get("session_key").asText();
            String unionid = json.has("unionid") ? json.get("unionid").asText() : null;

            return new WxSession(openid, sessionKey, unionid, 0, null);
        } catch (Exception e) {
            log.error("code2session error", e);
            return new WxSession(null, null, null, -1, e.getMessage());
        }
    }

    /**
     * 微信会话信息
     */
    public record WxSession(String openid, String sessionKey, String unionid, int errcode, String errmsg) {
        public boolean isSuccess() {
            return errcode == 0 && openid != null;
        }
    }

    /**
     * 获取接口调用凭证access_token
     */
    public String getAccessToken() {
        String appId = getAppId();
        String appSecret = getAppSecret();

        if (appId.isEmpty() || appSecret.isEmpty()) {
            log.error("微信小程序配置未设置");
            return null;
        }

        String url = String.format(GET_ACCESS_TOKEN_URL, appId, appSecret);

        try {
            String response = restTemplate.getForObject(url, String.class);
            log.debug("getAccessToken response: {}", response);

            JsonNode json = objectMapper.readTree(response);

            if (json.has("errcode") && json.get("errcode").asInt() != 0) {
                log.error("getAccessToken failed: {}", json.get("errmsg").asText());
                return null;
            }

            return json.get("access_token").asText();
        } catch (Exception e) {
            log.error("getAccessToken error", e);
            return null;
        }
    }

    /**
     * 通过phoneCode获取用户手机号
     */
    public PhoneInfo getPhoneNumber(String phoneCode) {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            return new PhoneInfo(null, -1, "获取access_token失败");
        }

        String url = String.format(GET_PHONE_NUMBER_URL, accessToken);

        try {
            // 构建请求体
            String requestBody = objectMapper.writeValueAsString(java.util.Map.of("code", phoneCode));
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(url, entity, String.class);
            log.debug("getPhoneNumber response: {}", response);

            JsonNode json = objectMapper.readTree(response);

            if (json.has("errcode") && json.get("errcode").asInt() != 0) {
                int errcode = json.get("errcode").asInt();
                String errmsg = json.has("errmsg") ? json.get("errmsg").asText() : "未知错误";
                log.error("getPhoneNumber failed: {} - {}", errcode, errmsg);
                return new PhoneInfo(null, errcode, errmsg);
            }

            JsonNode phoneInfo = json.get("phone_info");
            String phoneNumber = phoneInfo.get("phoneNumber").asText();

            return new PhoneInfo(phoneNumber, 0, null);
        } catch (Exception e) {
            log.error("getPhoneNumber error", e);
            return new PhoneInfo(null, -1, e.getMessage());
        }
    }

    /**
     * 手机号信息
     */
    public record PhoneInfo(String phoneNumber, int errcode, String errmsg) {
        public boolean isSuccess() {
            return errcode == 0 && phoneNumber != null;
        }
    }
}
