package tech.wetech.admin3.miniapp.service;

import tech.wetech.admin3.miniapp.dto.CreateOrderDTO;
import tech.wetech.admin3.miniapp.dto.UploadMaterialDTO;
import tech.wetech.admin3.miniapp.vo.OrderVO;
import tech.wetech.admin3.miniapp.vo.PageVO;
import tech.wetech.admin3.miniapp.vo.PaymentVO;

import java.util.List;
import java.util.Map;

/**
 * 小程序订单服务接口
 */
public interface IMiniappOrderService {
    
    /**
     * 创建订单
     */
    OrderVO createOrder(Long userId, CreateOrderDTO dto);
    
    /**
     * 获取用户订单列表
     */
    PageVO<OrderVO> getOrderList(Long userId, Integer status, int page, int size);
    
    /**
     * 获取订单详情
     */
    OrderVO getOrderDetail(Long userId, Long orderId);
    
    /**
     * 获取订单材料列表
     */
    List<OrderVO.MaterialVO> getMaterials(Long userId, Long orderId);
    
    /**
     * 上传订单材料
     */
    OrderVO.MaterialVO uploadMaterial(Long userId, Long orderId, UploadMaterialDTO dto);
    
    /**
     * 删除订单材料
     */
    void deleteMaterial(Long userId, Long orderId, Long materialId);
    
    /**
     * 取消订单
     */
    void cancelOrder(Long userId, Long orderId);
    
    /**
     * 申请退款
     */
    void applyRefund(Long userId, Long orderId, String reason);
    
    /**
     * 发起支付（获取小程序支付参数）
     */
    PaymentVO prepay(Long userId, Long orderId);
    
    /**
     * 处理支付回调
     */
    void handlePayNotify(String orderNo, String transactionId, String tradeState);
    
    /**
     * 查询支付状态
     */
    Map<String, Object> queryPayStatus(Long userId, Long orderId);
}
