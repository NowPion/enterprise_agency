package tech.wetech.admin3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.common.authz.RequiresPermissions;
import tech.wetech.admin3.sys.model.Invoice;
import tech.wetech.admin3.sys.service.InvoiceService;

import java.util.Map;

@Tag(name = "发票管理", description = "发票管理接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Operation(summary = "获取发票列表")
    @RequiresPermissions("invoice:view")
    @GetMapping
    public ResponseEntity<Page<Invoice>> findAll(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        if (status != null && !status.isEmpty()) {
            return ResponseEntity.ok(invoiceService.findByStatus(status, pageRequest));
        }
        return ResponseEntity.ok(invoiceService.findAll(pageRequest));
    }

    @Operation(summary = "获取发票详情")
    @RequiresPermissions("invoice:view")
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> findById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findById(id));
    }

    @Operation(summary = "开票")
    @RequiresPermissions("invoice:issue")
    @PostMapping("/{id}/issue")
    public ResponseEntity<Invoice> issue(@PathVariable Long id, @RequestBody @Valid IssueRequest request) {
        return ResponseEntity.ok(invoiceService.issue(id, request.invoiceNo(), request.invoiceUrl()));
    }

    @Operation(summary = "开票失败")
    @RequiresPermissions("invoice:issue")
    @PostMapping("/{id}/fail")
    public ResponseEntity<Invoice> fail(@PathVariable Long id, @RequestBody @Valid FailRequest request) {
        return ResponseEntity.ok(invoiceService.fail(id, request.reason()));
    }

    @Operation(summary = "发票统计")
    @RequiresPermissions("invoice:view")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
            "pendingCount", invoiceService.countByStatus("pending"),
            "issuedCount", invoiceService.countByStatus("issued"),
            "failedCount", invoiceService.countByStatus("failed")
        ));
    }

    record IssueRequest(@NotBlank String invoiceNo, @NotBlank String invoiceUrl) {}
    record FailRequest(@NotBlank String reason) {}
}
