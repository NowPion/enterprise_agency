package tech.wetech.admin3.sys.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.Order;
import tech.wetech.admin3.sys.model.Refund;
import tech.wetech.admin3.sys.model.Transaction;
import tech.wetech.admin3.sys.repository.OrderRepository;
import tech.wetech.admin3.sys.repository.RefundRepository;
import tech.wetech.admin3.sys.repository.TransactionRepository;
import tech.wetech.admin3.wechat.WechatPayClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RefundService {

    private static final Logger log = LoggerFactory.getLogger(RefundService.class);
    
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final WechatPayClient wechatPayClient;
    private static final AtomicLong counter = new AtomicLong(0);

    public RefundService(RefundRepository refundRepository, OrderRepository orderRepository, 
                        TransactionRepository transactionRepository,
                        WechatPayClient wechatPayClient) {
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
        this.wechatPayClient = wechatPayClient;
    }

    public Page<Refund> findAll(Pageable pageable) {
        return refundRepository.findAll(pageable);
    }

    public Page<Refund> findByStatus(String status, Pageable pageable) {
        return refundRepository.findByStatus(status, pageable);
    }

    public Refund findById(Long id) {
        return refundRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "退款记录不存在"));
    }

    public List<Refund> findByOrderId(Long orderId) {
        return refundRepository.findByOrderIdOrderByCreateTimeDesc(orderId);
    }

    // 创建退款申请
    @Transactional
    public Refund createRefund(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "订单不存在"));
        
        if (!"paid".equals(order.getPayStatus())) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "订单未支付，无法退款");
        }

        Refund refund = new Refund();
        refund.setRefundNo(generateRefundNo());
        refund.setOrderId(orderId);
        refund.setOrderNo(order.getOrderNo());
        refund.setUserId(order.getUserId());
        refund.setRefundAmount(order.getPayAmount());
        refund.setOrderAmount(order.getAmount());
        refund.setRefundReason(reason);
        refund.setStatus("pending");
        
        order.setStatus("refunding");
        orderRepository.save(order);
        
        return refundRepository.save(refund);
    }

    // 审批退款
    @Transactional
    public Refund approve(Long id, Long operatorId, String operatorName) {
        Refund refund = findById(id);
        if (!"pending".equals(refund.getStatus())) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "退款状态不正确");
        }
        
        // 获取订单信息
        Order order = orderRepository.findById(refund.getOrderId())
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "订单不存在"));
        
        // 检查订单是否有微信支付交易号
        if (order.getTransactionId() == null || order.getTransactionId().isEmpty()) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "订单未找到微信支付交易号，无法退款");
        }
        
        // 调用微信退款API
        try {
            log.info("开始调用微信退款API, 退款单号: {}, 订单号: {}, 微信交易号: {}", 
                    refund.getRefundNo(), order.getOrderNo(), order.getTransactionId());
            
            // 金额转换：元 -> 分
            int refundAmountFen = refund.getRefundAmount().multiply(new BigDecimal("100")).intValue();
            int totalAmountFen = order.getPayAmount().multiply(new BigDecimal("100")).intValue();
            
            WechatPayClient.RefundResult result = wechatPayClient.refund(
                order.getOrderNo(),           // 商户订单号
                refund.getRefundNo(),         // 商户退款单号
                refundAmountFen,              // 退款金额（分）
                totalAmountFen,               // 订单总金额（分）
                refund.getRefundReason()      // 退款原因
            );
            
            if (!result.success()) {
                log.error("微信退款API调用失败: {}", result.errorMessage());
                throw new BusinessException(CommonResultStatus.FAIL, "微信退款失败: " + result.errorMessage());
            }
            
            log.info("微信退款API调用成功, 微信退款单号: {}, 状态: {}", result.refundId(), result.status());
            
            // 更新退款记录
            refund.setStatus("processing");
            refund.setWechatRefundId(result.refundId());
            refund.setOperatorId(operatorId);
            refund.setOperatorName(operatorName);
            
            // 如果微信返回的状态是SUCCESS，直接标记为成功
            if ("SUCCESS".equals(result.status())) {
                refund.setStatus("success");
                refund.setRefundTime(LocalDateTime.now());
                
                // 更新订单状态
                order.setStatus("refunded");
                order.setPayStatus("refunded");
                orderRepository.save(order);
            }
            
            return refundRepository.save(refund);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信退款API异常", e);
            throw new BusinessException(CommonResultStatus.FAIL, "退款处理失败: " + e.getMessage());
        }
    }

    // 拒绝退款
    @Transactional
    public Refund reject(Long id, String reason, Long operatorId, String operatorName) {
        Refund refund = findById(id);
        if (!"pending".equals(refund.getStatus())) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "退款状态不正确");
        }
        refund.setStatus("rejected");
        refund.setRejectReason(reason);
        refund.setOperatorId(operatorId);
        refund.setOperatorName(operatorName);
        
        // 恢复订单状态
        Order order = orderRepository.findById(refund.getOrderId()).orElse(null);
        if (order != null) {
            order.setStatus("paid");
            orderRepository.save(order);
        }
        
        return refundRepository.save(refund);
    }

    // 完成退款
    @Transactional
    public Refund complete(Long id, String wechatRefundId) {
        Refund refund = findById(id);
        
        // 如果没有提供微信退款单号，尝试查询微信退款状态
        if ((wechatRefundId == null || wechatRefundId.isEmpty()) && refund.getRefundNo() != null) {
            try {
                log.info("查询微信退款状态: {}", refund.getRefundNo());
                WechatPayClient.RefundResult result = wechatPayClient.queryRefund(refund.getRefundNo());
                
                if (result.success()) {
                    wechatRefundId = result.refundId();
                    String status = result.status();
                    log.info("微信退款状态查询成功: refundId={}, status={}", wechatRefundId, status);
                    
                    // 根据微信返回的状态判断
                    if ("SUCCESS".equals(status)) {
                        // 退款成功
                        refund.setStatus("success");
                        refund.setWechatRefundId(wechatRefundId);
                        refund.setRefundTime(LocalDateTime.now());
                        
                        // 更新订单状态
                        Order order = orderRepository.findById(refund.getOrderId()).orElse(null);
                        if (order != null) {
                            order.setStatus("refunded");
                            order.setPayStatus("refunded");
                            orderRepository.save(order);
                        }
                        
                        return refundRepository.save(refund);
                    } else if ("PROCESSING".equals(status)) {
                        throw new BusinessException(CommonResultStatus.FAIL, "退款仍在处理中，请稍后再试");
                    } else if ("CLOSED".equals(status) || "ABNORMAL".equals(status)) {
                        throw new BusinessException(CommonResultStatus.FAIL, "微信退款失败: " + status);
                    }
                } else {
                    log.warn("查询微信退款状态失败: {}", result.errorMessage());
                }
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                log.error("查询微信退款状态异常", e);
            }
        }
        
        // 手动完成退款（不查询微信状态）
        refund.setStatus("success");
        if (wechatRefundId != null && !wechatRefundId.isEmpty()) {
            refund.setWechatRefundId(wechatRefundId);
        }
        refund.setRefundTime(LocalDateTime.now());
        
        // 更新订单状态
        Order order = orderRepository.findById(refund.getOrderId()).orElse(null);
        if (order != null) {
            order.setStatus("refunded");
            order.setPayStatus("refunded");
            orderRepository.save(order);
        }
        
        return refundRepository.save(refund);
    }

    // 查询微信退款状态并同步
    @Transactional
    public Refund syncRefundStatus(Long id) {
        Refund refund = findById(id);
        
        if (refund.getRefundNo() == null || refund.getRefundNo().isEmpty()) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "退款单号不存在");
        }
        
        try {
            log.info("同步微信退款状态: {}", refund.getRefundNo());
            WechatPayClient.RefundResult result = wechatPayClient.queryRefund(refund.getRefundNo());
            
            if (!result.success()) {
                throw new BusinessException(CommonResultStatus.FAIL, "查询微信退款状态失败: " + result.errorMessage());
            }
            
            String wechatRefundId = result.refundId();
            String status = result.status();
            log.info("微信退款状态: refundId={}, status={}", wechatRefundId, status);
            
            // 更新微信退款单号
            if (wechatRefundId != null && !wechatRefundId.isEmpty()) {
                refund.setWechatRefundId(wechatRefundId);
            }
            
            // 根据微信返回的状态更新本地状态
            if ("SUCCESS".equals(status)) {
                refund.setStatus("success");
                refund.setRefundTime(LocalDateTime.now());
                
                // 更新订单状态
                Order order = orderRepository.findById(refund.getOrderId()).orElse(null);
                if (order != null) {
                    order.setStatus("refunded");
                    order.setPayStatus("refunded");
                    orderRepository.save(order);
                }
            } else if ("PROCESSING".equals(status)) {
                refund.setStatus("processing");
            } else if ("CLOSED".equals(status) || "ABNORMAL".equals(status)) {
                refund.setStatus("failed");
                refund.setFailReason("微信退款失败: " + status);
                
                // 恢复订单状态
                Order order = orderRepository.findById(refund.getOrderId()).orElse(null);
                if (order != null && "refunding".equals(order.getStatus())) {
                    order.setStatus("paid");
                    orderRepository.save(order);
                }
            }
            
            return refundRepository.save(refund);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("同步微信退款状态异常", e);
            throw new BusinessException(CommonResultStatus.FAIL, "同步退款状态失败: " + e.getMessage());
        }
    }

    // 处理微信退款回调
    @Transactional
    public void handleRefundNotify(String outRefundNo, String wechatRefundId, String refundStatus) {
        Refund refund = refundRepository.findByRefundNo(outRefundNo).orElse(null);
        if (refund == null) {
            // 尝试用wechatRefundId查找
            refund = refundRepository.findByWechatRefundId(wechatRefundId).orElse(null);
        }
        
        if (refund == null) {
            throw new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "退款记录不存在: " + outRefundNo);
        }
        
        // 已处理过的退款不再重复处理
        if ("success".equals(refund.getStatus()) || "failed".equals(refund.getStatus())) {
            return;
        }
        
        if ("SUCCESS".equals(refundStatus)) {
            // 退款成功
            refund.setStatus("success");
            refund.setWechatRefundId(wechatRefundId);
            refund.setRefundTime(LocalDateTime.now());
            
            // 更新订单状态
            Order order = orderRepository.findById(refund.getOrderId()).orElse(null);
            if (order != null) {
                order.setStatus("refunded");
                order.setPayStatus("refunded");
                orderRepository.save(order);
            }
            
            // 创建退款交易记录
            Transaction transaction = new Transaction();
            transaction.setTransactionNo(generateTransactionNo());
            transaction.setOrderId(refund.getOrderId());
            transaction.setOrderNo(refund.getOrderNo());
            transaction.setUserId(refund.getUserId());
            transaction.setTransactionType("refund");
            transaction.setAmount(refund.getRefundAmount());
            transaction.setPayChannel("wechat");
            transaction.setWechatTransactionId(wechatRefundId);
            transaction.setStatus("success");
            transaction.setRemark("订单退款成功");
            transactionRepository.save(transaction);
            log.info("退款交易记录已创建: transactionNo={}, refundNo={}, amount={}", 
                    transaction.getTransactionNo(), outRefundNo, refund.getRefundAmount());
        } else if ("CLOSED".equals(refundStatus) || "ABNORMAL".equals(refundStatus)) {
            // 退款关闭或异常
            refund.setStatus("failed");
            refund.setRejectReason("微信退款失败: " + refundStatus);
            
            // 恢复订单状态
            Order order = orderRepository.findById(refund.getOrderId()).orElse(null);
            if (order != null && "refunding".equals(order.getStatus())) {
                order.setStatus("paid");
                orderRepository.save(order);
            }
        }
        // PROCESSING状态不做处理，等待最终结果
        
        refundRepository.save(refund);
    }

    private String generateRefundNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "RF" + date + String.format("%04d", counter.incrementAndGet() % 10000);
    }

    private String generateTransactionNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "TXN" + timestamp + random;
    }
}
