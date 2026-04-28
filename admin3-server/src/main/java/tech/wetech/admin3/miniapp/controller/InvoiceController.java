package tech.wetech.admin3.miniapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.miniapp.ApiResult;
import tech.wetech.admin3.miniapp.auth.MiniappAuth;
import tech.wetech.admin3.miniapp.dto.ApplyInvoiceDTO;
import tech.wetech.admin3.miniapp.service.IMiniappInvoiceService;
import tech.wetech.admin3.miniapp.vo.InvoiceVO;
import tech.wetech.admin3.miniapp.vo.PageVO;

import java.util.HashMap;
import java.util.Map;

/**
 * 小程序发票接口
 */
@Tag(name = "小程序-发票", description = "小程序发票相关接口")
@RestController("miniappInvoiceController")
@RequestMapping("/minapp/invoice")
public class InvoiceController {

    private final IMiniappInvoiceService invoiceService;

    public InvoiceController(IMiniappInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Operation(summary = "申请发票")
    @PostMapping("/apply")
    public ApiResult<InvoiceVO> applyInvoice(@MiniappAuth Long userId, @RequestBody ApplyInvoiceDTO dto) {
        return ApiResult.ok(invoiceService.applyInvoice(userId, dto));
    }

    @Operation(summary = "获取发票列表")
    @GetMapping("/list")
    public ApiResult<PageVO<InvoiceVO>> getInvoiceList(
            @MiniappAuth Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResult.ok(invoiceService.getInvoiceList(userId, page, size));
    }

    @Operation(summary = "获取发票详情")
    @GetMapping("/detail/{id}")
    public ApiResult<InvoiceVO> getInvoiceDetail(@MiniappAuth Long userId, @PathVariable Long id) {
        return ApiResult.ok(invoiceService.getInvoiceDetail(userId, id));
    }

    @Operation(summary = "检查订单是否可开票")
    @GetMapping("/check/{orderId}")
    public ApiResult<Map<String, Object>> checkCanApply(@MiniappAuth Long userId, @PathVariable Long orderId) {
        boolean canApply = invoiceService.canApplyInvoice(userId, orderId);
        Map<String, Object> result = new HashMap<>();
        result.put("canApply", canApply);
        return ApiResult.ok(result);
    }
}
