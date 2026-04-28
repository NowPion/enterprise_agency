package tech.wetech.admin3.miniapp.dto;

/**
 * 创建订单DTO
 */
public class CreateOrderDTO {
    private Long productId;
    private Long yearPriceId;  // 年度价格ID
    private String formData;  // JSON格式的表单数据
    private String remark;
    private Long userCouponId; // 使用的优惠券ID

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getYearPriceId() { return yearPriceId; }
    public void setYearPriceId(Long yearPriceId) { this.yearPriceId = yearPriceId; }
    public String getFormData() { return formData; }
    public void setFormData(String formData) { this.formData = formData; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Long getUserCouponId() { return userCouponId; }
    public void setUserCouponId(Long userCouponId) { this.userCouponId = userCouponId; }
}
