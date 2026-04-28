package tech.wetech.admin3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.wetech.admin3.sys.repository.MiniappUserRepository;
import tech.wetech.admin3.sys.repository.OrderRepository;
import tech.wetech.admin3.sys.repository.ProductRepository;
import tech.wetech.admin3.sys.repository.RefundRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "仪表盘", description = "首页统计数据")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/dashboard")
public class DashboardController {

    private final MiniappUserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final RefundRepository refundRepository;

    public DashboardController(MiniappUserRepository userRepository,
                               OrderRepository orderRepository,
                               ProductRepository productRepository,
                               RefundRepository refundRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.refundRepository = refundRepository;
    }

    @Operation(summary = "获取统计概览")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计
        long totalUsers = userRepository.count();
        long todayUsers = userRepository.countByCreateTimeAfter(LocalDate.now().atStartOfDay());
        
        // 订单统计
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus("pending");
        long processingOrders = orderRepository.countByStatus("processing");
        long completedOrders = orderRepository.countByStatus("completed");
        
        // 计算总收入
        BigDecimal totalRevenue = orderRepository.findAll().stream()
            .filter(o -> "paid".equals(o.getPayStatus()) || "completed".equals(o.getStatus()))
            .map(o -> o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 产品统计
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.findByStatusOrderBySortDesc(1).size();
        
        // 退款统计
        long pendingRefunds = refundRepository.findByStatus("pending", null).getTotalElements();
        
        stats.put("totalUsers", totalUsers);
        stats.put("todayUsers", todayUsers);
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("processingOrders", processingOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalProducts", totalProducts);
        stats.put("activeProducts", activeProducts);
        stats.put("pendingRefunds", pendingRefunds);
        
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "获取近7天用户增长趋势")
    @GetMapping("/user-trend")
    public ResponseEntity<List<Map<String, Object>>> getUserTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            
            long count = userRepository.countByCreateTimeBetween(start, end);
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("count", count);
            trend.add(item);
        }
        
        return ResponseEntity.ok(trend);
    }

    @Operation(summary = "获取近7天订单趋势")
    @GetMapping("/order-trend")
    public ResponseEntity<List<Map<String, Object>>> getOrderTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            
            long count = orderRepository.findAll().stream()
                .filter(o -> o.getCreateTime() != null 
                    && !o.getCreateTime().isBefore(start) 
                    && o.getCreateTime().isBefore(end))
                .count();
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("count", count);
            trend.add(item);
        }
        
        return ResponseEntity.ok(trend);
    }

    @Operation(summary = "获取订单状态分布")
    @GetMapping("/order-status")
    public ResponseEntity<List<Map<String, Object>>> getOrderStatusDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();
        
        String[] statuses = {"pending", "paid", "reviewing", "processing", "completed", "cancelled", "refunded"};
        String[] labels = {"待付款", "已付款", "审核中", "办理中", "已完成", "已取消", "已退款"};
        
        for (int i = 0; i < statuses.length; i++) {
            long count = orderRepository.countByStatus(statuses[i]);
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", labels[i]);
                item.put("value", count);
                distribution.add(item);
            }
        }
        
        return ResponseEntity.ok(distribution);
    }

    @Operation(summary = "获取产品销量排行")
    @GetMapping("/product-ranking")
    public ResponseEntity<List<Map<String, Object>>> getProductRanking() {
        List<Map<String, Object>> ranking = new ArrayList<>();
        
        productRepository.findAll().forEach(product -> {
            long orderCount = orderRepository.findAll().stream()
                .filter(o -> product.getId().equals(o.getProductId()))
                .count();
            
            Map<String, Object> item = new HashMap<>();
            item.put("name", product.getName());
            item.put("count", orderCount);
            ranking.add(item);
        });
        
        // 按订单数量降序排序
        ranking.sort((a, b) -> Long.compare((Long) b.get("count"), (Long) a.get("count")));
        
        return ResponseEntity.ok(ranking.size() > 10 ? ranking.subList(0, 10) : ranking);
    }
}
