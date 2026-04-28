package tech.wetech.admin3.wechat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.service.SystemConfigService;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * 微信支付服务 - 企业付款到零钱
 * 使用微信支付V3 API: 商家转账到零钱
 */
@Service
public class WechatPayService {

    private static final Logger log = LoggerFactory.getLogger(WechatPayService.class);
    
    // 商家转账到零钱 API
    private static final String TRANSFER_BATCHES_URL = "https://api.mch.weixin.qq.com/v3/transfer/batches";
    
    private final SystemConfigService configService;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public WechatPayService(SystemConfigService configService) {
        this.configService = configService;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    /**
     * 商家转账到零钱
     * @param openid 用户openid
     * @param amount 金额(元)
     * @param outBatchNo 商户批次单号
     * @param outDetailNo 商户明细单号
     * @param transferRemark 转账备注
     * @return 转账结果
     */
    public TransferResult transferToBalance(String openid, BigDecimal amount, 
            String outBatchNo, String outDetailNo, String transferRemark) {
        
        String appId = configService.getValue(WechatClient.KEY_MINIAPP_APP_ID, "");
        String mchId = configService.getValue(WechatClient.KEY_PAY_MCH_ID, "");
        String privateKeyContent = configService.getValue(WechatClient.KEY_PAY_PRIVATE_KEY, "");
        String certSerialNo = configService.getValue(WechatClient.KEY_PAY_CERT_SERIAL_NO, "");

        if (appId.isEmpty() || mchId.isEmpty() || privateKeyContent.isEmpty() || certSerialNo.isEmpty()) {
            log.error("微信支付配置不完整");
            return new TransferResult(false, null, null, "微信支付配置不完整");
        }

        try {
            // 金额转为分
            int amountInFen = amount.multiply(new BigDecimal("100")).intValue();
            
            // 构建请求体
            String requestBody = buildTransferRequest(appId, outBatchNo, outDetailNo, 
                    openid, amountInFen, transferRemark);
            
            log.info("转账请求: outBatchNo={}, openid={}, amount={}分", outBatchNo, openid, amountInFen);
            
            // 生成签名
            String timestamp = String.valueOf(Instant.now().getEpochSecond());
            String nonceStr = UUID.randomUUID().toString().replace("-", "");
            String signature = generateSignature("POST", "/v3/transfer/batches", 
                    timestamp, nonceStr, requestBody, privateKeyContent);
            
            // 构建Authorization头
            String authorization = String.format(
                "WECHATPAY2-SHA256-RSA2048 mchid=\"%s\",nonce_str=\"%s\",signature=\"%s\",timestamp=\"%s\",serial_no=\"%s\"",
                mchId, nonceStr, signature, timestamp, certSerialNo);
            
            // 发送请求
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TRANSFER_BATCHES_URL))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", authorization)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            log.info("转账响应: status={}, body={}", response.statusCode(), response.body());
            
            return parseTransferResponse(response.statusCode(), response.body());
            
        } catch (Exception e) {
            log.error("转账失败", e);
            return new TransferResult(false, null, null, "转账异常: " + e.getMessage());
        }
    }

    /**
     * 构建转账请求体
     */
    private String buildTransferRequest(String appId, String outBatchNo, String outDetailNo,
            String openid, int amountInFen, String transferRemark) {
        
        return String.format("""
            {
                "appid": "%s",
                "out_batch_no": "%s",
                "batch_name": "提现",
                "batch_remark": "用户提现",
                "total_amount": %d,
                "total_num": 1,
                "transfer_detail_list": [{
                    "out_detail_no": "%s",
                    "transfer_amount": %d,
                    "transfer_remark": "%s",
                    "openid": "%s"
                }]
            }
            """, appId, outBatchNo, amountInFen, outDetailNo, amountInFen, 
            transferRemark != null ? transferRemark : "提现", openid);
    }

    /**
     * 生成签名
     */
    private String generateSignature(String method, String url, String timestamp, 
            String nonceStr, String body, String privateKeyContent) throws Exception {
        
        String message = method + "\n" + url + "\n" + timestamp + "\n" + nonceStr + "\n" + body + "\n";
        
        PrivateKey privateKey = loadPrivateKey(privateKeyContent);
        
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(message.getBytes(StandardCharsets.UTF_8));
        
        return Base64.getEncoder().encodeToString(sign.sign());
    }

    /**
     * 加载私钥
     */
    private PrivateKey loadPrivateKey(String privateKeyContent) throws Exception {
        String privateKeyPEM = privateKeyContent
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 解析转账响应
     */
    private TransferResult parseTransferResponse(int statusCode, String responseBody) {
        try {
            JsonNode json = objectMapper.readTree(responseBody);
            
            if (statusCode == 200) {
                String batchId = json.has("batch_id") ? json.get("batch_id").asText() : null;
                String outBatchNo = json.has("out_batch_no") ? json.get("out_batch_no").asText() : null;
                return new TransferResult(true, batchId, outBatchNo, null);
            } else {
                String code = json.has("code") ? json.get("code").asText() : "UNKNOWN";
                String message = json.has("message") ? json.get("message").asText() : "未知错误";
                return new TransferResult(false, null, null, code + ": " + message);
            }
        } catch (Exception e) {
            return new TransferResult(false, null, null, "解析响应失败: " + e.getMessage());
        }
    }

    /**
     * 查询转账批次单
     */
    public TransferQueryResult queryTransferBatch(String outBatchNo) {
        String mchId = configService.getValue(WechatClient.KEY_PAY_MCH_ID, "");
        String privateKeyContent = configService.getValue(WechatClient.KEY_PAY_PRIVATE_KEY, "");
        String certSerialNo = configService.getValue(WechatClient.KEY_PAY_CERT_SERIAL_NO, "");

        if (mchId.isEmpty() || privateKeyContent.isEmpty() || certSerialNo.isEmpty()) {
            return new TransferQueryResult(false, null, "配置不完整");
        }

        try {
            String url = "/v3/transfer/batches/out-batch-no/" + outBatchNo + "?need_query_detail=true";
            String fullUrl = "https://api.mch.weixin.qq.com" + url;
            
            String timestamp = String.valueOf(Instant.now().getEpochSecond());
            String nonceStr = UUID.randomUUID().toString().replace("-", "");
            String signature = generateSignature("GET", url, timestamp, nonceStr, "", privateKeyContent);
            
            String authorization = String.format(
                "WECHATPAY2-SHA256-RSA2048 mchid=\"%s\",nonce_str=\"%s\",signature=\"%s\",timestamp=\"%s\",serial_no=\"%s\"",
                mchId, nonceStr, signature, timestamp, certSerialNo);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Accept", "application/json")
                .header("Authorization", authorization)
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                String batchStatus = json.has("batch_status") ? json.get("batch_status").asText() : null;
                return new TransferQueryResult(true, batchStatus, null);
            } else {
                return new TransferQueryResult(false, null, response.body());
            }
        } catch (Exception e) {
            log.error("查询转账批次失败", e);
            return new TransferQueryResult(false, null, e.getMessage());
        }
    }

    /**
     * 转账结果
     */
    public record TransferResult(boolean success, String batchId, String outBatchNo, String errorMessage) {}

    /**
     * 转账查询结果
     */
    public record TransferQueryResult(boolean success, String batchStatus, String errorMessage) {}
}
