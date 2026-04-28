package tech.wetech.admin3.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.miniapp.ApiResult;
import tech.wetech.admin3.miniapp.service.IMiniappOrderService;
import tech.wetech.admin3.sys.service.RefundService;
import tech.wetech.admin3.wechat.WechatPayClient;

import java.io.BufferedReader;
import java.util.Map;

/**
 * 微信支付控制器
 */
@Tag(name = "微信支付")
@RestController
@RequestMapping("/minapp/wechat/pay")
public class WechatPayController {

    private static final Logger log = LoggerFactory.getLogger(WechatPayController.class);
    
    private final WechatPayClient wechatPayClient;
    private final IMiniappOrderService orderService;
    private final RefundService refundService;

    public WechatPayController(WechatPayClient wechatPayClient, IMiniappOrderService orderService, 
                               RefundService refundService) {
        this.wechatPayClient = wechatPayClient;
        this.orderService = orderService;
        this.refundService = refundService;
    }

    @Operation(summary = "JSAPI支付（小程序/公众号）")
    @PostMapping("/jsapi")
    public ApiResult<Map<String, String>> jsapiPay(@RequestBody JsapiPayRequest request) {
        String outTradeNo = WechatPayClient.generateOutTradeNo("JS");
        
        WechatPayClient.PayResult result = wechatPayClient.jsapiPay(
            outTradeNo, 
            request.total(), 
            request.description(), 
            request.openid()
        );
        
        if (!result.success()) {
            return ApiResult.fail(result.errorMessage());
        }
        
        Map<String, String> payParams = wechatPayClient.getJsapiPayParams(result.prepayId());
        if (payParams == null) {
            return ApiResult.fail("生成支付参数失败");
        }
        
        payParams.put("outTradeNo", outTradeNo);
        return ApiResult.ok(payParams);
    }

    @Operation(summary = "Native支付（扫码支付）")
    @PostMapping("/native")
    public ApiResult<NativePayResponse> nativePay(@RequestBody NativePayRequest request) {
        String outTradeNo = WechatPayClient.generateOutTradeNo("N");
        
        WechatPayClient.PayResult result = wechatPayClient.nativePay(
            outTradeNo, 
            request.total(), 
            request.description()
        );
        
        if (!result.success()) {
            return ApiResult.fail(result.errorMessage());
        }
        
        return ApiResult.ok(new NativePayResponse(outTradeNo, result.url()));
    }

    @Operation(summary = "H5支付")
    @PostMapping("/h5")
    public ApiResult<H5PayResponse> h5Pay(@RequestBody H5PayRequest request, HttpServletRequest httpRequest) {
        String outTradeNo = WechatPayClient.generateOutTradeNo("H5");
        String clientIp = getClientIp(httpRequest);
        
        WechatPayClient.PayResult result = wechatPayClient.h5Pay(
            outTradeNo, 
            request.total(), 
            request.description(),
            clientIp
        );
        
        if (!result.success()) {
            return ApiResult.fail(result.errorMessage());
        }
        
        return ApiResult.ok(new H5PayResponse(outTradeNo, result.url()));
    }

    @Operation(summary = "查询订单")
    @GetMapping("/order/{outTradeNo}")
    public ApiResult<WechatPayClient.OrderQueryResult> queryOrder(@PathVariable String outTradeNo) {
        WechatPayClient.OrderQueryResult result = wechatPayClient.queryOrderByOutTradeNo(outTradeNo);
        if (!result.success()) {
            return ApiResult.fail(result.errorMessage());
        }
        return ApiResult.ok(result);
    }

    @Operation(summary = "关闭订单")
    @PostMapping("/order/{outTradeNo}/close")
    public ApiResult<Void> closeOrder(@PathVariable String outTradeNo) {
        boolean success = wechatPayClient.closeOrder(outTradeNo);
        if (!success) {
            return ApiResult.fail("关闭订单失败");
        }
        return ApiResult.ok("关闭成功");
    }

    @Operation(summary = "申请退款")
    @PostMapping("/refund")
    public ApiResult<WechatPayClient.RefundResult> refund(@RequestBody RefundRequest request) {
        String outRefundNo = WechatPayClient.generateOutTradeNo("R");
        
        WechatPayClient.RefundResult result = wechatPayClient.refund(
            request.outTradeNo(),
            outRefundNo,
            request.refundAmount(),
            request.totalAmount(),
            request.reason()
        );
        
        if (!result.success()) {
            return ApiResult.fail(result.errorMessage());
        }
        return ApiResult.ok(result);
    }

    @Operation(summary = "查询退款")
    @GetMapping("/refund/{outRefundNo}")
    public ApiResult<WechatPayClient.RefundResult> queryRefund(@PathVariable String outRefundNo) {
        WechatPayClient.RefundResult result = wechatPayClient.queryRefund(outRefundNo);
        if (!result.success()) {
            return ApiResult.fail(result.errorMessage());
        }
        return ApiResult.ok(result);
    }

    @Operation(summary = "支付回调通知")
    @PostMapping("/notify")
    public NotifyResponse payNotify(HttpServletRequest request) {
        try {
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            String nonce = request.getHeader("Wechatpay-Nonce");
            
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }
            
            log.info("收到支付回调: timestamp={}, nonce={}", timestamp, nonce);
            
            JsonNode data = wechatPayClient.decryptNotify(timestamp, nonce, body.toString());
            if (data == null) {
                return new NotifyResponse("FAIL", "解密失败");
            }
            
            String outTradeNo = data.get("out_trade_no").asText();
            String tradeState = data.get("trade_state").asText();
            String transactionId = data.has("transaction_id") ? data.get("transaction_id").asText() : null;
            
            log.info("支付回调解密成功: outTradeNo={}, tradeState={}", outTradeNo, tradeState);
            
            // 处理业务逻辑，更新订单状态
            orderService.handlePayNotify(outTradeNo, transactionId, tradeState);
            
            return new NotifyResponse("SUCCESS", null);
        } catch (Exception e) {
            log.error("处理支付回调失败", e);
            return new NotifyResponse("FAIL", e.getMessage());
        }
    }

    @Operation(summary = "退款回调通知")
    @PostMapping("/refund-notify")
    public NotifyResponse refundNotify(HttpServletRequest request) {
        try {
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            String nonce = request.getHeader("Wechatpay-Nonce");
            
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }
            
            log.info("收到退款回调: timestamp={}, nonce={}", timestamp, nonce);
            
            JsonNode data = wechatPayClient.decryptNotify(timestamp, nonce, body.toString());
            if (data == null) {
                return new NotifyResponse("FAIL", "解密失败");
            }
            
            String outRefundNo = data.get("out_refund_no").asText();
            String refundId = data.has("refund_id") ? data.get("refund_id").asText() : null;
            String refundStatus = data.get("refund_status").asText();
            
            log.info("退款回调解密成功: outRefundNo={}, refundStatus={}", outRefundNo, refundStatus);
            
            // 处理退款业务逻辑
            refundService.handleRefundNotify(outRefundNo, refundId, refundStatus);
            
            return new NotifyResponse("SUCCESS", null);
        } catch (Exception e) {
            log.error("处理退款回调失败", e);
            return new NotifyResponse("FAIL", e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    // ==================== 请求/响应类 ====================

    public record JsapiPayRequest(int total, String description, String openid) {}
    public record NativePayRequest(int total, String description) {}
    public record H5PayRequest(int total, String description) {}
    public record RefundRequest(String outTradeNo, int refundAmount, int totalAmount, String reason) {}
    
    public record NativePayResponse(String outTradeNo, String codeUrl) {}
    public record H5PayResponse(String outTradeNo, String h5Url) {}
    public record NotifyResponse(String code, String message) {}
}
