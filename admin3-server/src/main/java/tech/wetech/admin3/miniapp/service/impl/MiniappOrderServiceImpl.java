package tech.wetech.admin3.miniapp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.miniapp.dto.CreateOrderDTO;
import tech.wetech.admin3.miniapp.dto.UploadMaterialDTO;
import tech.wetech.admin3.miniapp.service.IMiniappCouponService;
import tech.wetech.admin3.miniapp.service.IMiniappOrderService;
import tech.wetech.admin3.miniapp.vo.OrderVO;
import tech.wetech.admin3.miniapp.vo.PageVO;
import tech.wetech.admin3.miniapp.vo.PaymentVO;
import tech.wetech.admin3.sys.model.*;
import tech.wetech.admin3.sys.repository.*;
import tech.wetech.admin3.wechat.WechatPayClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class MiniappOrderServiceImpl implements IMiniappOrderService {

    private static final Logger log = LoggerFactory.getLogger(MiniappOrderServiceImpl.class);
    private static final String DEV_MOCK_OPENID = "dev_test_openid_001";

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderProgressRepository orderProgressRepository;
    private final OrderMaterialRepository orderMaterialRepository;
    private final RefundRepository refundRepository;
    private final MiniappUserRepository miniappUserRepository;
    private final UserCouponRepository userCouponRepository;
    private final ProductYearPriceRepository productYearPriceRepository;
    private final TransactionRepository transactionRepository;
    private final WechatPayClient wechatPayClient;

    public MiniappOrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository,
                                   OrderProgressRepository orderProgressRepository, 
                                   OrderMaterialRepository orderMaterialRepository,
                                   RefundRepository refundRepository,
                                   MiniappUserRepository miniappUserRepository,
                                   UserCouponRepository userCouponRepository,
                                   ProductYearPriceRepository productYearPriceRepository,
                                   TransactionRepository transactionRepository,
                                   WechatPayClient wechatPayClient) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderProgressRepository = orderProgressRepository;
        this.orderMaterialRepository = orderMaterialRepository;
        this.refundRepository = refundRepository;
        this.miniappUserRepository = miniappUserRepository;
        this.userCouponRepository = userCouponRepository;
        this.productYearPriceRepository = productYearPriceRepository;
        this.transactionRepository = transactionRepository;
        this.wechatPayClient = wechatPayClient;
    }

    @Override
    @Transactional
    public OrderVO createOrder(Long userId, CreateOrderDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "产品不存在"));
        
        if (product.getStatus() != 1) {
            throw new BusinessException(CommonResultStatus.FAIL, "产品已下架");
        }

        BigDecimal orderAmount = product.getPrice();
        String yearInfo = null;
        
        // 如果选择了年度价格，使用年度价格
        if (dto.getYearPriceId() != null) {
            ProductYearPrice yearPrice = productYearPriceRepository.findById(dto.getYearPriceId())
                    .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "年度价格不存在"));
            
            if (!yearPrice.getProductId().equals(product.getId())) {
                throw new BusinessException(CommonResultStatus.FAIL, "年度价格与产品不匹配");
            }
            if (yearPrice.getStatus() != 1) {
                throw new BusinessException(CommonResultStatus.FAIL, "该年度价格已下架");
            }
            
            orderAmount = yearPrice.getPrice();
            yearInfo = yearPrice.getYear();
        }
        
        BigDecimal discountAmount = BigDecimal.ZERO;
        Long userCouponId = null;
        
        // 处理优惠券
        if (dto.getUserCouponId() != null) {
            UserCoupon userCoupon = userCouponRepository.findById(dto.getUserCouponId())
                    .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "优惠券不存在"));
            
            if (!userCoupon.getUserId().equals(userId)) {
                throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无权使用此优惠券");
            }
            if (!"unused".equals(userCoupon.getStatus())) {
                throw new BusinessException(CommonResultStatus.FAIL, "优惠券不可用");
            }
            LocalDateTime now = LocalDateTime.now();
            if (userCoupon.getEndTime() != null && userCoupon.getEndTime().isBefore(now)) {
                throw new BusinessException(CommonResultStatus.FAIL, "优惠券已过期");
            }
            if (userCoupon.getMinAmount() != null && orderAmount.compareTo(userCoupon.getMinAmount()) < 0) {
                throw new BusinessException(CommonResultStatus.FAIL, "未达到优惠券最低消费金额");
            }
            
            // 计算优惠金额
            if ("fixed".equals(userCoupon.getCouponType())) {
                discountAmount = userCoupon.getValue();
            } else {
                discountAmount = orderAmount.multiply(BigDecimal.ONE.subtract(userCoupon.getValue()));
                if (userCoupon.getMaxDiscount() != null && discountAmount.compareTo(userCoupon.getMaxDiscount()) > 0) {
                    discountAmount = userCoupon.getMaxDiscount();
                }
            }
            if (discountAmount.compareTo(orderAmount) > 0) {
                discountAmount = orderAmount;
            }
            userCouponId = userCoupon.getId();
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setAmount(orderAmount);
        order.setDiscountAmount(discountAmount);
        order.setUserCouponId(userCouponId);
        order.setFormData(dto.getFormData());
        order.setRemark(dto.getRemark());
        order.setCreateTime(LocalDateTime.now());
        
        // 检查是否为开发测试用户，开发模式下直接设置为已支付
        MiniappUser user = miniappUserRepository.findById(userId).orElse(null);
        boolean isDevMode = user != null && DEV_MOCK_OPENID.equals(user.getOpenid());
        
        if (isDevMode) {
            // 开发模式：直接设置为已支付
            BigDecimal payAmount = orderAmount.subtract(discountAmount);
            order.setStatus("paid");
            order.setPayStatus("paid");
            order.setPayTime(LocalDateTime.now());
            order.setPayAmount(payAmount);
            order.setTransactionId("DEV_" + System.currentTimeMillis());
            log.info("开发模式：订单自动设置为已支付, orderNo={}", order.getOrderNo());
        } else {
            order.setStatus("pending"); // 待支付
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // 添加订单创建进度记录
        OrderProgress createProgress = new OrderProgress();
        createProgress.setOrderId(savedOrder.getId());
        createProgress.setOrderNo(savedOrder.getOrderNo());
        createProgress.setTitle("订单创建");
        createProgress.setContent("用户提交订单");
        createProgress.setStatus("completed");
        orderProgressRepository.save(createProgress);
        
        // 开发模式下添加支付成功进度记录
        if (isDevMode) {
            BigDecimal payAmount = orderAmount.subtract(discountAmount);
            OrderProgress payProgress = new OrderProgress();
            payProgress.setOrderId(savedOrder.getId());
            payProgress.setOrderNo(savedOrder.getOrderNo());
            payProgress.setTitle("支付成功");
            payProgress.setContent(String.format("用户完成支付，订单金额%.2f元", payAmount));
            payProgress.setStatus("completed");
            orderProgressRepository.save(payProgress);
        }
        
        // 开发模式下标记优惠券已使用（需要在订单保存后执行，因为需要orderId）
        if (isDevMode && userCouponId != null) {
            final Long finalOrderId = savedOrder.getId();
            final String finalOrderNo = savedOrder.getOrderNo();
            final BigDecimal finalDiscountAmount = discountAmount;
            userCouponRepository.findById(userCouponId).ifPresent(uc -> {
                uc.setStatus("used");
                uc.setOrderId(finalOrderId);
                uc.setOrderNo(finalOrderNo);
                uc.setDiscountAmount(finalDiscountAmount);
                uc.setUseTime(LocalDateTime.now());
                userCouponRepository.save(uc);
            });
        }
        
        return convertToVO(savedOrder);
    }

    @Override
    public PageVO<OrderVO> getOrderList(Long userId, Integer status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Order> orderPage;
        if (status != null && status >= 0) {
            String statusStr = getStatusString(status);
            orderPage = orderRepository.findByUserIdAndStatusOrderByCreateTimeDesc(userId, statusStr, pageRequest);
        } else {
            orderPage = orderRepository.findByUserId(userId, pageRequest);
        }
        
        List<OrderVO> list = orderPage.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return new PageVO<>(list, orderPage.getTotalElements());
    }

    @Override
    public OrderVO getOrderDetail(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无权查看此订单");
        }
        
        OrderVO vo = convertToVO(order);
        
        // 获取产品所需材料说明
        if (order.getProductId() != null) {
            productRepository.findById(order.getProductId()).ifPresent(product -> {
                vo.setRequiredMaterials(product.getMaterials());
            });
        }
        
        // 获取所有材料
        List<OrderMaterial> allMaterials = orderMaterialRepository.findByOrderIdOrderByCreateTimeDesc(orderId);
        
        // 按progressId分组管理员上传的材料
        Map<Long, List<OrderVO.MaterialVO>> adminMaterialsByProgress = allMaterials.stream()
                .filter(m -> "admin".equals(m.getUploadType()) && m.getProgressId() != null)
                .collect(Collectors.groupingBy(
                        OrderMaterial::getProgressId,
                        Collectors.mapping(this::convertMaterialToVO, Collectors.toList())
                ));
        
        // 获取进度列表，并关联管理员上传的材料
        List<OrderProgress> progressList = orderProgressRepository.findByOrderIdOrderByCreateTimeDesc(orderId);
        vo.setProgressList(progressList.stream().map(p -> {
            OrderVO.ProgressVO pvo = convertProgressToVO(p);
            pvo.setMaterials(adminMaterialsByProgress.get(p.getId()));
            return pvo;
        }).collect(Collectors.toList()));
        
        // 只返回用户上传的材料
        List<OrderVO.MaterialVO> userMaterials = allMaterials.stream()
                .filter(m -> "user".equals(m.getUploadType()))
                .map(this::convertMaterialToVO)
                .collect(Collectors.toList());
        vo.setMaterialList(userMaterials);
        
        return vo;
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无权操作此订单");
        }
        
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(CommonResultStatus.FAIL, "只能取消待支付的订单");
        }
        
        order.setStatus("cancelled"); // 已取消
        orderRepository.save(order);
    }

    @Override
    public List<OrderVO.MaterialVO> getMaterials(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无权查看此订单");
        }
        
        List<OrderMaterial> materialList = orderMaterialRepository.findByOrderIdOrderByCreateTimeDesc(orderId);
        return materialList.stream().map(this::convertMaterialToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderVO.MaterialVO uploadMaterial(Long userId, Long orderId, UploadMaterialDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无权操作此订单");
        }
        
        // 只有已支付、办理中的订单可以上传材料
        String status = order.getStatus();
        if (!"paid".equals(status) && !"reviewing".equals(status) && !"processing".equals(status)) {
            throw new BusinessException(CommonResultStatus.FAIL, "当前订单状态不支持上传材料");
        }
        
        OrderMaterial material = new OrderMaterial();
        material.setOrderId(orderId);
        material.setOrderNo(order.getOrderNo());
        material.setName(dto.getName());
        material.setMaterialType(dto.getMaterialType());
        material.setFileUrl(dto.getFileUrl());
        material.setFileType(dto.getFileType());
        material.setFileSize(dto.getFileSize());
        material.setUploadType("user");
        material.setUploaderId(userId);
        material.setRemark(dto.getRemark());
        
        material = orderMaterialRepository.save(material);
        return convertMaterialToVO(material);
    }

    @Override
    @Transactional
    public void deleteMaterial(Long userId, Long orderId, Long materialId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无权操作此订单");
        }
        
        OrderMaterial material = orderMaterialRepository.findById(materialId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "材料不存在"));
        
        // 只能删除自己上传的材料
        if (!"user".equals(material.getUploadType()) || !userId.equals(material.getUploaderId())) {
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无权删除此材料");
        }
        
        orderMaterialRepository.delete(material);
    }

    @Override
    @Transactional
    public void applyRefund(Long userId, Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无权操作此订单");
        }
        
        String status = order.getStatus();
        if (!"paid".equals(status) && !"reviewing".equals(status) && !"processing".equals(status)) {
            throw new BusinessException(CommonResultStatus.FAIL, "当前订单状态不支持退款");
        }
        
        // 检查是否已有退款申请
        if (refundRepository.existsByOrderIdAndStatusIn(orderId, List.of("pending", "processing"))) {
            throw new BusinessException(CommonResultStatus.FAIL, "已有退款申请在处理中");
        }
        
        Refund refund = new Refund();
        refund.setRefundNo(generateRefundNo());
        refund.setOrderId(orderId);
        refund.setOrderNo(order.getOrderNo());
        refund.setUserId(userId);
        refund.setRefundAmount(order.getAmount());
        refund.setOrderAmount(order.getAmount());
        refund.setRefundReason(reason);
        refund.setStatus("pending"); // 待审核
        refundRepository.save(refund);
        
        // 更新订单状态为退款中
        order.setStatus("refunding");
        orderRepository.save(order);
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "ORD" + timestamp + random;
    }

    private String generateRefundNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "REF" + timestamp + random;
    }

    private String generateTransactionNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "TXN" + timestamp + random;
    }

    private String getStatusString(Integer status) {
        return switch (status) {
            case 0 -> "pending";
            case 1 -> "paid";
            case 2 -> "processing";
            case 3 -> "completed";
            case 4 -> "refunding";
            case 5 -> "cancelled";
            default -> "pending";
        };
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setProductName(order.getProductName());
        vo.setAmount(order.getAmount());
        vo.setDiscountAmount(order.getDiscountAmount());
        // 计算实付金额：如果已有payAmount则使用，否则计算（订单金额 - 优惠金额）
        BigDecimal payAmount = order.getPayAmount();
        if (payAmount == null && order.getAmount() != null) {
            payAmount = order.getAmount();
            if (order.getDiscountAmount() != null) {
                payAmount = payAmount.subtract(order.getDiscountAmount());
            }
        }
        vo.setPayAmount(payAmount);
        vo.setStatus(getStatusCode(order.getStatus()));
        vo.setStatusText(getStatusText(order.getStatus()));
        vo.setFormData(order.getFormData());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setPayTime(order.getPayTime());
        vo.setCompleteTime(order.getCompleteTime());
        return vo;
    }

    private Integer getStatusCode(String status) {
        return switch (status) {
            case "pending" -> 0;
            case "paid", "reviewing" -> 1;
            case "processing" -> 2;
            case "completed" -> 3;
            case "refunding", "refunded" -> 4;
            case "cancelled" -> 5;
            default -> 0;
        };
    }

    private String getStatusText(String status) {
        return switch (status) {
            case "pending" -> "待支付";
            case "paid" -> "已支付";
            case "reviewing" -> "待审核";
            case "processing" -> "办理中";
            case "completed" -> "已完成";
            case "refunding" -> "退款中";
            case "refunded" -> "已退款";
            case "cancelled" -> "已取消";
            default -> "未知";
        };
    }

    private OrderVO.ProgressVO convertProgressToVO(OrderProgress progress) {
        OrderVO.ProgressVO vo = new OrderVO.ProgressVO();
        vo.setId(progress.getId());
        vo.setTitle(progress.getTitle());
        vo.setContent(progress.getContent());
        vo.setCreateTime(progress.getCreateTime());
        return vo;
    }

    private OrderVO.MaterialVO convertMaterialToVO(OrderMaterial material) {
        OrderVO.MaterialVO vo = new OrderVO.MaterialVO();
        vo.setId(material.getId());
        vo.setName(material.getName());
        vo.setFileUrl(material.getFileUrl());
        vo.setUploadTime(material.getCreateTime());
        return vo;
    }

    @Override
    @Transactional
    public PaymentVO prepay(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无权操作此订单");
        }
        
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(CommonResultStatus.FAIL, "订单状态不正确，无法支付");
        }
        
        // 获取用户openid
        MiniappUser user = miniappUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "用户不存在"));
        
        // 计算实付金额（订单金额 - 优惠金额）
        BigDecimal payAmount = order.getAmount();
        if (order.getDiscountAmount() != null) {
            payAmount = payAmount.subtract(order.getDiscountAmount());
        }
        
        // 金额转换为分
        int totalFen = payAmount.multiply(new BigDecimal("100")).intValue();
        
        // 调用微信支付JSAPI
        WechatPayClient.PayResult result = wechatPayClient.jsapiPay(
                order.getOrderNo(),
                totalFen,
                order.getProductName(),
                user.getOpenid()
        );
        
        if (!result.success()) {
            log.error("微信支付预下单失败: {}", result.errorMessage());
            throw new BusinessException(CommonResultStatus.FAIL, "支付发起失败: " + result.errorMessage());
        }
        
        // 获取小程序调起支付的参数
        Map<String, String> payParams = wechatPayClient.getJsapiPayParams(result.prepayId());
        if (payParams == null) {
            throw new BusinessException(CommonResultStatus.FAIL, "生成支付参数失败");
        }
        
        PaymentVO vo = new PaymentVO();
        vo.setOrderId(orderId);
        vo.setOrderNo(order.getOrderNo());
        vo.setAppId(payParams.get("appId"));
        vo.setTimeStamp(payParams.get("timeStamp"));
        vo.setNonceStr(payParams.get("nonceStr"));
        vo.setPackageValue(payParams.get("package"));
        vo.setSignType(payParams.get("signType"));
        vo.setPaySign(payParams.get("paySign"));
        
        return vo;
    }

    @Override
    @Transactional
    public void handlePayNotify(String orderNo, String transactionId, String tradeState) {
        Order order = orderRepository.findByOrderNo(orderNo).orElse(null);
        if (order == null) {
            log.warn("支付回调订单不存在: {}", orderNo);
            return;
        }
        
        if ("SUCCESS".equals(tradeState)) {
            // 支付成功
            if ("pending".equals(order.getStatus())) {
                // 计算实付金额（订单金额 - 优惠金额）
                BigDecimal payAmount = order.getAmount();
                if (order.getDiscountAmount() != null) {
                    payAmount = payAmount.subtract(order.getDiscountAmount());
                }
                
                order.setStatus("paid");
                order.setPayStatus("paid");
                order.setPayTime(LocalDateTime.now());
                order.setTransactionId(transactionId);
                order.setPayAmount(payAmount);
                orderRepository.save(order);
                
                // 添加支付成功进度记录
                OrderProgress payProgress = new OrderProgress();
                payProgress.setOrderId(order.getId());
                payProgress.setOrderNo(order.getOrderNo());
                payProgress.setTitle("支付成功");
                payProgress.setContent(String.format("用户完成支付，订单金额%.2f元", payAmount));
                payProgress.setStatus("completed");
                orderProgressRepository.save(payProgress);
                
                // 创建交易记录
                Transaction transaction = new Transaction();
                transaction.setTransactionNo(generateTransactionNo());
                transaction.setOrderId(order.getId());
                transaction.setOrderNo(order.getOrderNo());
                transaction.setUserId(order.getUserId());
                transaction.setTransactionType("pay");
                transaction.setAmount(payAmount);
                transaction.setPayChannel("wechat");
                transaction.setWechatTransactionId(transactionId);
                transaction.setStatus("success");
                transaction.setRemark("订单支付成功");
                transactionRepository.save(transaction);
                log.info("交易记录已创建: transactionNo={}, orderNo={}, amount={}", transaction.getTransactionNo(), orderNo, payAmount);
                
                // 标记优惠券已使用
                if (order.getUserCouponId() != null) {
                    userCouponRepository.findById(order.getUserCouponId()).ifPresent(userCoupon -> {
                        userCoupon.setStatus("used");
                        userCoupon.setOrderId(order.getId());
                        userCoupon.setOrderNo(order.getOrderNo());
                        userCoupon.setDiscountAmount(order.getDiscountAmount());
                        userCoupon.setUseTime(LocalDateTime.now());
                        userCouponRepository.save(userCoupon);
                        log.info("优惠券已使用: userCouponId={}, orderId={}", userCoupon.getId(), order.getId());
                    });
                }
                
                log.info("订单支付成功: orderNo={}, transactionId={}", orderNo, transactionId);
            }
        } else if ("CLOSED".equals(tradeState) || "PAYERROR".equals(tradeState)) {
            // 支付失败或关闭
            log.warn("订单支付失败: orderNo={}, tradeState={}", orderNo, tradeState);
        }
    }

    @Override
    public Map<String, Object> queryPayStatus(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无权查看此订单");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("orderNo", order.getOrderNo());
        result.put("status", order.getStatus());
        result.put("payStatus", order.getPayStatus());
        
        // 如果订单还是待支付状态，主动查询微信支付状态
        if ("pending".equals(order.getStatus())) {
            WechatPayClient.OrderQueryResult queryResult = wechatPayClient.queryOrderByOutTradeNo(order.getOrderNo());
            if (queryResult.success() && queryResult.isPaid()) {
                // 微信已支付但本地未更新，更新订单状态
                handlePayNotify(order.getOrderNo(), queryResult.transactionId(), "SUCCESS");
                result.put("status", "paid");
                result.put("payStatus", "paid");
            }
            result.put("wechatTradeState", queryResult.tradeState());
        }
        
        return result;
    }
}
