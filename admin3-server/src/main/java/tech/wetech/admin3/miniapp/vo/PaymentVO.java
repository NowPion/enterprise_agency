package tech.wetech.admin3.miniapp.vo;

/**
 * 小程序支付参数VO
 */
public class PaymentVO {
    
    private Long orderId;
    private String orderNo;
    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String packageValue; // package是Java关键字，用packageValue代替
    private String signType;
    private String paySign;

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getTimeStamp() { return timeStamp; }
    public void setTimeStamp(String timeStamp) { this.timeStamp = timeStamp; }
    public String getNonceStr() { return nonceStr; }
    public void setNonceStr(String nonceStr) { this.nonceStr = nonceStr; }
    public String getPackageValue() { return packageValue; }
    public void setPackageValue(String packageValue) { this.packageValue = packageValue; }
    public String getSignType() { return signType; }
    public void setSignType(String signType) { this.signType = signType; }
    public String getPaySign() { return paySign; }
    public void setPaySign(String paySign) { this.paySign = paySign; }
}
