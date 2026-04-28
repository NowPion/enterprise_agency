package tech.wetech.admin3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.common.authz.RequiresPermissions;
import tech.wetech.admin3.sys.model.Refund;
import tech.wetech.admin3.sys.service.RefundService;

@Tag(name = "退款管理", description = "退款管理接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @Operation(summary = "获取退款列表")
    @RequiresPermissions("refund:view")
    @GetMapping
    public ResponseEntity<Page<Refund>> findAll(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        if (status != null && !status.isEmpty()) {
            return ResponseEntity.ok(refundService.findByStatus(status, pageRequest));
        }
        return ResponseEntity.ok(refundService.findAll(pageRequest));
    }

    @Operation(summary = "获取退款详情")
    @RequiresPermissions("refund:view")
    @GetMapping("/{id}")
    public ResponseEntity<Refund> findById(@PathVariable Long id) {
        return ResponseEntity.ok(refundService.findById(id));
    }

    @Operation(summary = "审批退款")
    @RequiresPermissions("refund:approve")
    @PostMapping("/{id}/approve")
    public ResponseEntity<Refund> approve(@PathVariable Long id, @RequestBody @Valid ApproveRequest request) {
        return ResponseEntity.ok(refundService.approve(id, request.operatorId(), request.operatorName()));
    }

    @Operation(summary = "拒绝退款")
    @RequiresPermissions("refund:approve")
    @PostMapping("/{id}/reject")
    public ResponseEntity<Refund> reject(@PathVariable Long id, @RequestBody @Valid RejectRequest request) {
        return ResponseEntity.ok(refundService.reject(id, request.reason(), request.operatorId(), request.operatorName()));
    }

    @Operation(summary = "完成退款")
    @RequiresPermissions("refund:approve")
    @PostMapping("/{id}/complete")
    public ResponseEntity<Refund> complete(@PathVariable Long id, @RequestBody CompleteRequest request) {
        return ResponseEntity.ok(refundService.complete(id, request.wechatRefundId()));
    }

    @Operation(summary = "同步微信退款状态")
    @RequiresPermissions("refund:approve")
    @PostMapping("/{id}/sync")
    public ResponseEntity<Refund> syncStatus(@PathVariable Long id) {
        return ResponseEntity.ok(refundService.syncRefundStatus(id));
    }

    record ApproveRequest(@NotNull Long operatorId, @NotBlank String operatorName) {}
    record RejectRequest(@NotBlank String reason, @NotNull Long operatorId, @NotBlank String operatorName) {}
    record CompleteRequest(String wechatRefundId) {}
}
