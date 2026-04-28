package tech.wetech.admin3.miniapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.miniapp.ApiResult;
import tech.wetech.admin3.miniapp.auth.MiniappAuth;
import tech.wetech.admin3.miniapp.dto.CreateOrderDTO;
import tech.wetech.admin3.miniapp.dto.UploadMaterialDTO;
import tech.wetech.admin3.miniapp.service.IMiniappOrderService;
import tech.wetech.admin3.miniapp.vo.OrderVO;
import tech.wetech.admin3.miniapp.vo.PageVO;
import tech.wetech.admin3.miniapp.vo.PaymentVO;

import java.util.List;
import java.util.Map;

/**
 * 小程序订单接口
 */
@Tag(name = "小程序-订单", description = "小程序订单相关接口")
@RestController("miniappOrderController")
@RequestMapping("/minapp/order")
public class OrderController {

    private final IMiniappOrderService orderService;

    public OrderController(IMiniappOrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public ApiResult<OrderVO> createOrder(@MiniappAuth Long userId, @RequestBody CreateOrderDTO dto) {
        return ApiResult.ok(orderService.createOrder(userId, dto));
    }

    @Operation(summary = "获取订单列表")
    @GetMapping("/list")
    public ApiResult<PageVO<OrderVO>> getOrderList(
            @MiniappAuth Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResult.ok(orderService.getOrderList(userId, status, page, size));
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/detail/{id}")
    public ApiResult<OrderVO> getOrderDetail(@MiniappAuth Long userId, @PathVariable Long id) {
        return ApiResult.ok(orderService.getOrderDetail(userId, id));
    }

    @Operation(summary = "获取订单材料列表")
    @GetMapping("/{id}/materials")
    public ApiResult<List<OrderVO.MaterialVO>> getMaterials(@MiniappAuth Long userId, @PathVariable Long id) {
        return ApiResult.ok(orderService.getMaterials(userId, id));
    }

    @Operation(summary = "上传订单材料")
    @PostMapping("/{id}/materials")
    public ApiResult<OrderVO.MaterialVO> uploadMaterial(@MiniappAuth Long userId, @PathVariable Long id,
                                                        @RequestBody UploadMaterialDTO dto) {
        return ApiResult.ok(orderService.uploadMaterial(userId, id, dto));
    }

    @Operation(summary = "删除订单材料")
    @DeleteMapping("/{orderId}/materials/{materialId}")
    public ApiResult<Void> deleteMaterial(@MiniappAuth Long userId, @PathVariable Long orderId,
                                          @PathVariable Long materialId) {
        orderService.deleteMaterial(userId, orderId, materialId);
        return ApiResult.ok("删除成功");
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel/{id}")
    public ApiResult<Void> cancelOrder(@MiniappAuth Long userId, @PathVariable Long id) {
        orderService.cancelOrder(userId, id);
        return ApiResult.ok("取消成功");
    }

    @Operation(summary = "申请退款")
    @PostMapping("/refund/{id}")
    public ApiResult<Void> applyRefund(@MiniappAuth Long userId, @PathVariable Long id, 
                                       @RequestParam String reason) {
        orderService.applyRefund(userId, id, reason);
        return ApiResult.ok("退款申请已提交");
    }

    @Operation(summary = "发起支付")
    @PostMapping("/pay/{id}")
    public ApiResult<PaymentVO> prepay(@MiniappAuth Long userId, @PathVariable Long id) {
        return ApiResult.ok(orderService.prepay(userId, id));
    }

    @Operation(summary = "查询支付状态")
    @GetMapping("/pay-status/{id}")
    public ApiResult<Map<String, Object>> queryPayStatus(@MiniappAuth Long userId, @PathVariable Long id) {
        return ApiResult.ok(orderService.queryPayStatus(userId, id));
    }
}
