package tech.wetech.admin3.wechat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.wetech.admin3.sys.service.SystemConfigService;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 微信支付V3 API客户端
 * 支持：Native支付、JSAPI支付、H5支付、退款、订单查询
 */
@Component
public class WechatPayClient {

    private static final Logger log = LoggerFactory.getLogger(WechatPayClient.class);
    private static final String BASE_URL = "https://api.mch.weixin.qq.com";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final SystemConfigService configService;
    private final HttpClient httpClient;

    public WechatPayClient(SystemConfigService configService) {
        this.configService = configService;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    /**
     * 获取配置值
     */
    private String getConfig(String key) {
        return configService.getValue(key, "");
    }

    private String getAppId() {
        return getConfig(WechatClient.KEY_MINIAPP_APP_ID);
    }

    private String getMchId() {
        return getConfig(WechatClient.KEY_PAY_MCH_ID);
    }

    private String getApiV3Key() {
        return getConfig(WechatClient.KEY_PAY_API_V3_KEY);
    }

    private String getSerialNo() {
        return getConfig(WechatClient.KEY_PAY_CERT_SERIAL_NO);
    }

    private String getPrivateKeyContent() {
        return getConfig(WechatClient.KEY_PAY_PRIVATE_KEY);
    }

    private String getNotifyUrl() {
        return getConfig(WechatClient.KEY_PAY_NOTIFY_URL);
    }

    private String getRefundNotifyUrl() {
        return getConfig(WechatClient.KEY_PAY_REFUND_NOTIFY_URL);
    }

    /**
     * 加载商户私钥
     */
    private PrivateKey loadPrivateKey() throws Exception {
        String content = getPrivateKeyContent();
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("微信支付私钥未配置");
        }

        String privateKeyPEM = content
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 生成随机字符串
     */
    private String generateNonceStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成时间戳
     */
    private String generateTimestamp() {
        return String.valueOf(Instant.now().getEpochSecond());
    }

    /**
     * 使用私钥签名
     */
    private String sign(String message) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(loadPrivateKey());
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 构建Authorization头
     */
    private String buildAuthorization(String method, String url, String body) throws Exception {
        String timestamp = generateTimestamp();
        String nonceStr = generateNonceStr();

        String signStr = method + "\n" + url + "\n" + timestamp + "\n" + nonceStr + "\n" + body + "\n";
        String signature = sign(signStr);

        return String.format(
                "WECHATPAY2-SHA256-RSA2048 mchid=\"%s\",nonce_str=\"%s\",signature=\"%s\",timestamp=\"%s\",serial_no=\"%s\"",
                getMchId(), nonceStr, signature, timestamp, getSerialNo()
        );
    }

    /**
     * 发送HTTP请求
     */
    private JsonNode request(String method, String url, ObjectNode body) throws Exception {
        String bodyStr = body != null ? objectMapper.writeValueAsString(body) : "";
        String authorization = buildAuthorization(method, url, bodyStr);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", authorization);

        HttpRequest request;
        if ("GET".equals(method)) {
            request = requestBuilder.GET().build();
        } else if ("POST".equals(method)) {
            request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(bodyStr)).build();
        } else {
            throw new IllegalArgumentException("不支持的HTTP方法: " + method);
        }

        log.debug("微信支付请求: {} {}", method, url);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.debug("微信支付响应: {} - {}", response.statusCode(), response.body());

        if (response.statusCode() == 200 || response.statusCode() == 204) {
            return response.body().isEmpty() ? objectMapper.createObjectNode() : objectMapper.readTree(response.body());
        } else {
            throw new RuntimeException("微信支付请求失败: " + response.statusCode() + " - " + response.body());
        }
    }

    /**
     * Native支付（扫码支付）
     * @param outTradeNo 商户订单号
     * @param total 订单金额，单位：分
     * @param description 商品描述
     * @return 包含code_url的响应
     */
    public PayResult nativePay(String outTradeNo, int total, String description) {
        return nativePay(outTradeNo, total, description, null);
    }

    public PayResult nativePay(String outTradeNo, int total, String description, String timeExpire) {
        try {
            String url = "/v3/pay/transactions/native";

            ObjectNode body = objectMapper.createObjectNode();
            body.put("appid", getAppId());
            body.put("mchid", getMchId());
            body.put("description", description);
            body.put("out_trade_no", outTradeNo);
            body.put("notify_url", getNotifyUrl());

            ObjectNode amount = objectMapper.createObjectNode();
            amount.put("total", total);
            amount.put("currency", "CNY");
            body.set("amount", amount);

            if (timeExpire != null) {
                body.put("time_expire", timeExpire);
            }

            JsonNode result = request("POST", url, body);
            String codeUrl = result.has("code_url") ? result.get("code_url").asText() : null;
            return new PayResult(true, codeUrl, null, null);
        } catch (Exception e) {
            log.error("Native支付失败", e);
            return new PayResult(false, null, null, e.getMessage());
        }
    }

    /**
     * JSAPI支付（公众号/小程序支付）
     * @param outTradeNo 商户订单号
     * @param total 订单金额，单位：分
     * @param description 商品描述
     * @param openid 用户openid
     * @return 包含prepay_id的响应
     */
    public PayResult jsapiPay(String outTradeNo, int total, String description, String openid) {
        return jsapiPay(outTradeNo, total, description, openid, null);
    }

    public PayResult jsapiPay(String outTradeNo, int total, String description, String openid, String timeExpire) {
        try {
            String url = "/v3/pay/transactions/jsapi";

            ObjectNode body = objectMapper.createObjectNode();
            body.put("appid", getAppId());
            body.put("mchid", getMchId());
            body.put("description", description);
            body.put("out_trade_no", outTradeNo);
            body.put("notify_url", getNotifyUrl());

            ObjectNode amount = objectMapper.createObjectNode();
            amount.put("total", total);
            amount.put("currency", "CNY");
            body.set("amount", amount);

            ObjectNode payer = objectMapper.createObjectNode();
            payer.put("openid", openid);
            body.set("payer", payer);

            if (timeExpire != null) {
                body.put("time_expire", timeExpire);
            }

            JsonNode result = request("POST", url, body);
            String prepayId = result.has("prepay_id") ? result.get("prepay_id").asText() : null;
            return new PayResult(true, null, prepayId, null);
        } catch (Exception e) {
            log.error("JSAPI支付失败", e);
            return new PayResult(false, null, null, e.getMessage());
        }
    }

    /**
     * H5支付
     * @param outTradeNo 商户订单号
     * @param total 订单金额，单位：分
     * @param description 商品描述
     * @param payerClientIp 用户IP
     * @return 包含h5_url的响应
     */
    public PayResult h5Pay(String outTradeNo, int total, String description, String payerClientIp) {
        return h5Pay(outTradeNo, total, description, payerClientIp, "Wap", null);
    }

    public PayResult h5Pay(String outTradeNo, int total, String description,
                           String payerClientIp, String h5Type, String timeExpire) {
        try {
            String url = "/v3/pay/transactions/h5";

            ObjectNode body = objectMapper.createObjectNode();
            body.put("appid", getAppId());
            body.put("mchid", getMchId());
            body.put("description", description);
            body.put("out_trade_no", outTradeNo);
            body.put("notify_url", getNotifyUrl());

            ObjectNode amount = objectMapper.createObjectNode();
            amount.put("total", total);
            amount.put("currency", "CNY");
            body.set("amount", amount);

            ObjectNode sceneInfo = objectMapper.createObjectNode();
            sceneInfo.put("payer_client_ip", payerClientIp);
            ObjectNode h5Info = objectMapper.createObjectNode();
            h5Info.put("type", h5Type);
            sceneInfo.set("h5_info", h5Info);
            body.set("scene_info", sceneInfo);

            if (timeExpire != null) {
                body.put("time_expire", timeExpire);
            }

            JsonNode result = request("POST", url, body);
            String h5Url = result.has("h5_url") ? result.get("h5_url").asText() : null;
            return new PayResult(true, h5Url, null, null);
        } catch (Exception e) {
            log.error("H5支付失败", e);
            return new PayResult(false, null, null, e.getMessage());
        }
    }

    /**
     * 根据商户订单号查询订单
     */
    public OrderQueryResult queryOrderByOutTradeNo(String outTradeNo) {
        try {
            String url = "/v3/pay/transactions/out-trade-no/" + outTradeNo + "?mchid=" + getMchId();
            JsonNode result = request("GET", url, null);
            return parseOrderResult(result);
        } catch (Exception e) {
            log.error("查询订单失败", e);
            return new OrderQueryResult(false, null, null, null, 0, e.getMessage());
        }
    }

    /**
     * 根据微信支付订单号查询订单
     */
    public OrderQueryResult queryOrderByTransactionId(String transactionId) {
        try {
            String url = "/v3/pay/transactions/id/" + transactionId + "?mchid=" + getMchId();
            JsonNode result = request("GET", url, null);
            return parseOrderResult(result);
        } catch (Exception e) {
            log.error("查询订单失败", e);
            return new OrderQueryResult(false, null, null, null, 0, e.getMessage());
        }
    }

    private OrderQueryResult parseOrderResult(JsonNode result) {
        String tradeState = result.has("trade_state") ? result.get("trade_state").asText() : null;
        String transactionId = result.has("transaction_id") ? result.get("transaction_id").asText() : null;
        String outTradeNo = result.has("out_trade_no") ? result.get("out_trade_no").asText() : null;
        int total = 0;
        if (result.has("amount") && result.get("amount").has("total")) {
            total = result.get("amount").get("total").asInt();
        }
        return new OrderQueryResult(true, tradeState, transactionId, outTradeNo, total, null);
    }

    /**
     * 关闭订单
     */
    public boolean closeOrder(String outTradeNo) {
        try {
            String url = "/v3/pay/transactions/out-trade-no/" + outTradeNo + "/close";
            ObjectNode body = objectMapper.createObjectNode();
            body.put("mchid", getMchId());
            request("POST", url, body);
            return true;
        } catch (Exception e) {
            log.error("关闭订单失败", e);
            return false;
        }
    }

    /**
     * 申请退款
     * @param outTradeNo 原商户订单号
     * @param outRefundNo 商户退款单号
     * @param refundAmount 退款金额，单位：分
     * @param totalAmount 原订单金额，单位：分
     * @return 退款响应
     */
    public RefundResult refund(String outTradeNo, String outRefundNo, int refundAmount, int totalAmount) {
        return refund(outTradeNo, outRefundNo, refundAmount, totalAmount, null);
    }

    public RefundResult refund(String outTradeNo, String outRefundNo, int refundAmount, int totalAmount, String reason) {
        try {
            String url = "/v3/refund/domestic/refunds";

            ObjectNode body = objectMapper.createObjectNode();
            body.put("out_trade_no", outTradeNo);
            body.put("out_refund_no", outRefundNo);
            
            // 添加退款回调通知URL
            String refundNotifyUrl = getRefundNotifyUrl();
            if (refundNotifyUrl != null && !refundNotifyUrl.isEmpty()) {
                body.put("notify_url", refundNotifyUrl);
            }

            ObjectNode amount = objectMapper.createObjectNode();
            amount.put("refund", refundAmount);
            amount.put("total", totalAmount);
            amount.put("currency", "CNY");
            body.set("amount", amount);

            if (reason != null) {
                body.put("reason", reason);
            }

            JsonNode result = request("POST", url, body);
            String refundId = result.has("refund_id") ? result.get("refund_id").asText() : null;
            String status = result.has("status") ? result.get("status").asText() : null;
            return new RefundResult(true, refundId, outRefundNo, status, null);
        } catch (Exception e) {
            log.error("申请退款失败", e);
            return new RefundResult(false, null, null, null, e.getMessage());
        }
    }

    /**
     * 查询退款
     */
    public RefundResult queryRefund(String outRefundNo) {
        try {
            String url = "/v3/refund/domestic/refunds/" + outRefundNo;
            JsonNode result = request("GET", url, null);
            String refundId = result.has("refund_id") ? result.get("refund_id").asText() : null;
            String status = result.has("status") ? result.get("status").asText() : null;
            return new RefundResult(true, refundId, outRefundNo, status, null);
        } catch (Exception e) {
            log.error("查询退款失败", e);
            return new RefundResult(false, null, null, null, e.getMessage());
        }
    }

    /**
     * 获取JSAPI调起支付的参数（小程序/公众号前端使用）
     * @param prepayId 预支付交易会话标识
     * @return 前端调起支付所需的参数
     */
    public Map<String, String> getJsapiPayParams(String prepayId) {
        try {
            String timestamp = generateTimestamp();
            String nonceStr = generateNonceStr();
            String packageStr = "prepay_id=" + prepayId;

            String signStr = getAppId() + "\n" + timestamp + "\n" + nonceStr + "\n" + packageStr + "\n";
            String paySign = sign(signStr);

            Map<String, String> params = new HashMap<>();
            params.put("appId", getAppId());
            params.put("timeStamp", timestamp);
            params.put("nonceStr", nonceStr);
            params.put("package", packageStr);
            params.put("signType", "RSA");
            params.put("paySign", paySign);

            return params;
        } catch (Exception e) {
            log.error("生成JSAPI支付参数失败", e);
            return null;
        }
    }

    /**
     * 验证支付回调通知签名并解密数据
     * @param timestamp 时间戳
     * @param nonce 随机串
     * @param body 请求体
     * @return 解密后的数据
     */
    public JsonNode decryptNotify(String timestamp, String nonce, String body) {
        try {
            JsonNode bodyJson = objectMapper.readTree(body);
            JsonNode resource = bodyJson.get("resource");

            String ciphertext = resource.get("ciphertext").asText();
            String nonceStr = resource.get("nonce").asText();
            String associatedData = resource.has("associated_data") ? resource.get("associated_data").asText() : "";

            byte[] key = getApiV3Key().getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, nonceStr.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            if (!associatedData.isEmpty()) {
                cipher.updateAAD(associatedData.getBytes(StandardCharsets.UTF_8));
            }

            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            return objectMapper.readTree(new String(decrypted, StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("解密回调数据失败", e);
            return null;
        }
    }

    /**
     * 生成商户订单号
     * @param prefix 订单号前缀
     * @return 订单号
     */
    public static String generateOutTradeNo(String prefix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits()) % 1000000);
        return prefix + timestamp + String.format("%06d", Integer.parseInt(random));
    }

    // ==================== 结果类 ====================

    /**
     * 支付结果
     */
    public record PayResult(boolean success, String url, String prepayId, String errorMessage) {}

    /**
     * 订单查询结果
     */
    public record OrderQueryResult(boolean success, String tradeState, String transactionId, 
                                   String outTradeNo, int total, String errorMessage) {
        public boolean isPaid() {
            return "SUCCESS".equals(tradeState);
        }
    }

    /**
     * 退款结果
     */
    public record RefundResult(boolean success, String refundId, String outRefundNo, 
                               String status, String errorMessage) {}
}
