package tech.wetech.admin3.sys.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.Order;
import tech.wetech.admin3.sys.model.OrderMaterial;
import tech.wetech.admin3.sys.model.OrderProgress;
import tech.wetech.admin3.sys.repository.OrderMaterialRepository;
import tech.wetech.admin3.sys.repository.OrderProgressRepository;
import tech.wetech.admin3.sys.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProgressRepository orderProgressRepository;
    private final OrderMaterialRepository orderMaterialRepository;

    public OrderService(OrderRepository orderRepository, 
                       OrderProgressRepository orderProgressRepository,
                       OrderMaterialRepository orderMaterialRepository) {
        this.orderRepository = orderRepository;
        this.orderProgressRepository = orderProgressRepository;
        this.orderMaterialRepository = orderMaterialRepository;
    }

    public Page<Order> search(String keyword, String status, Long assigneeId, Long userId, Pageable pageable) {
        return orderRepository.search(keyword, status, assigneeId, userId, pageable);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "订单不存在"));
    }

    public Order findByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "订单不存在"));
    }

    // 审核订单
    @Transactional
    public Order review(Long id, boolean approved, String remark) {
        Order order = findById(id);
        if (!"paid".equals(order.getStatus())) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "只有已付款订单才能审核");
        }
        order.setStatus(approved ? "reviewing" : "cancelled");
        order.setRemark(remark);
        addProgress(order, approved ? "订单审核通过" : "订单审核未通过", remark);
        return orderRepository.save(order);
    }

    // 派单
    @Transactional
    public Order assign(Long id, Long assigneeId, String assigneeName) {
        Order order = findById(id);
        if (!"reviewing".equals(order.getStatus()) && !"paid".equals(order.getStatus())) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "当前订单状态不能派单");
        }
        order.setAssigneeId(assigneeId);
        order.setAssigneeName(assigneeName);
        order.setAssignTime(LocalDateTime.now());
        order.setStatus("processing");
        addProgress(order, "订单已派单", "办理人员: " + assigneeName);
        return orderRepository.save(order);
    }

    // 更新进度
    @Transactional
    public OrderProgress updateProgress(Long id, String title, String content) {
        Order order = findById(id);
        return addProgress(order, title, content);
    }

    // 完成订单
    @Transactional
    public Order complete(Long id, String remark) {
        Order order = findById(id);
        if (!"processing".equals(order.getStatus())) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "只有办理中的订单才能完成");
        }
        order.setStatus("completed");
        order.setCompleteTime(LocalDateTime.now());
        order.setRemark(remark);
        addProgress(order, "订单已完成", remark);
        return orderRepository.save(order);
    }

    // 取消订单
    @Transactional
    public Order cancel(Long id, String reason) {
        Order order = findById(id);
        order.setStatus("cancelled");
        order.setCancelReason(reason);
        addProgress(order, "订单已取消", reason);
        return orderRepository.save(order);
    }

    private OrderProgress addProgress(Order order, String title, String content) {
        OrderProgress progress = new OrderProgress();
        progress.setOrderId(order.getId());
        progress.setOrderNo(order.getOrderNo());
        progress.setTitle(title);
        progress.setContent(content);
        progress.setStatus("completed");
        return orderProgressRepository.save(progress);
    }

    // 获取订单进度
    public List<OrderProgress> getProgress(Long orderId) {
        return orderProgressRepository.findByOrderIdOrderByCreateTimeDesc(orderId);
    }

    // 获取订单材料
    public List<OrderMaterial> getMaterials(Long orderId) {
        return orderMaterialRepository.findByOrderIdOrderByCreateTimeDesc(orderId);
    }

    // 上传材料
    @Transactional
    public OrderMaterial uploadMaterial(OrderMaterial material) {
        return orderMaterialRepository.save(material);
    }

    @Transactional
    public void deleteMaterial(Long id) {
        orderMaterialRepository.deleteById(id);
    }

    // 统计
    public long countByStatus(String status) {
        return orderRepository.countByStatus(status);
    }

    public long countAll() {
        return orderRepository.count();
    }
    
    public Map<String, Object> getStats(Long assigneeId) {
        if (assigneeId != null) {
            // 办理人员只统计自己的订单
            return Map.of(
                "total", orderRepository.countByAssigneeId(assigneeId),
                "pending", 0L, // 办理人员看不到待付款订单
                "paid", 0L,    // 办理人员看不到已付款未派单订单
                "processing", orderRepository.countByAssigneeIdAndStatus(assigneeId, "processing"),
                "completed", orderRepository.countByAssigneeIdAndStatus(assigneeId, "completed")
            );
        }
        return Map.of(
            "total", orderRepository.count(),
            "pending", orderRepository.countByStatus("pending"),
            "paid", orderRepository.countByStatus("paid"),
            "processing", orderRepository.countByStatus("processing"),
            "completed", orderRepository.countByStatus("completed")
        );
    }
}
