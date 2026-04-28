package tech.wetech.admin3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.common.authz.RequiresPermissions;
import tech.wetech.admin3.sys.model.Transaction;
import tech.wetech.admin3.sys.service.TransactionService;

import java.util.List;
import java.util.Map;

@Tag(name = "交易记录管理", description = "交易记录管理接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "获取交易记录列表")
    @RequiresPermissions("transaction:view")
    @GetMapping
    public ResponseEntity<Page<Transaction>> findAll(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        if (type != null && !type.isEmpty()) {
            return ResponseEntity.ok(transactionService.findByType(type, pageRequest));
        }
        return ResponseEntity.ok(transactionService.findAll(pageRequest));
    }

    @Operation(summary = "获取交易记录详情")
    @RequiresPermissions("transaction:view")
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> findById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @Operation(summary = "根据订单获取交易记录")
    @RequiresPermissions("transaction:view")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Transaction>> findByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(transactionService.findByOrderId(orderId));
    }

    @Operation(summary = "交易统计")
    @RequiresPermissions("transaction:view")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
            "totalPayAmount", transactionService.sumPayAmount(),
            "totalRefundAmount", transactionService.sumRefundAmount(),
            "successCount", transactionService.countByStatus("success"),
            "pendingCount", transactionService.countByStatus("pending")
        ));
    }
}
