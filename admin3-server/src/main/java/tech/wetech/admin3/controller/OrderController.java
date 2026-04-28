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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.common.Constants;
import tech.wetech.admin3.common.SessionItemHolder;
import tech.wetech.admin3.common.authz.RequiresPermissions;
import tech.wetech.admin3.sys.model.Order;
import tech.wetech.admin3.sys.model.OrderMaterial;
import tech.wetech.admin3.sys.model.OrderProgress;
import tech.wetech.admin3.sys.model.User;
import tech.wetech.admin3.sys.repository.RoleRepository;
import tech.wetech.admin3.sys.service.OrderService;
import tech.wetech.admin3.sys.service.dto.UserinfoDTO;

import java.util.List;
import java.util.Map;

@Tag(name = "订单管理", description = "订单管理接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/orders")
public class OrderController {

    private final OrderService orderService;
    private final RoleRepository roleRepository;

    public OrderController(OrderService orderService, RoleRepository roleRepository) {
        this.orderService = orderService;
        this.roleRepository = roleRepository;
    }

    @Operation(summary = "获取办理人员列表")
    @RequiresPermissions("order:view")
    @GetMapping("/assignees")
    public ResponseEntity<List<Map<String, Object>>> getAssignees() {
        List<User> users = roleRepository.findUsersByRoleName("办理人员");
        List<Map<String, Object>> result = users.stream()
            .map(u -> Map.<String, Object>of("id", u.getId(), "username", u.getUsername()))
            .toList();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "搜索订单")
    @RequiresPermissions("order:view")
    @GetMapping
    public ResponseEntity<Page<Order>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        
        // 获取当前用户
        UserinfoDTO userInfo = (UserinfoDTO) SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
        Long assigneeId = null;
        
        // 判断是否是办理人员（只有order:update权限，没有order:assign权限）
        if (userInfo != null && userInfo.permissions() != null) {
            boolean hasAssignPermission = userInfo.permissions().contains("order:assign");
            boolean hasUpdatePermission = userInfo.permissions().contains("order:update");
            // 办理人员：有update权限但没有assign权限
            if (hasUpdatePermission && !hasAssignPermission) {
                assigneeId = userInfo.userId();
            }
        }
        
        return ResponseEntity.ok(orderService.search(keyword, status, assigneeId, userId, pageRequest));
    }

    @Operation(summary = "获取订单详情")
    @RequiresPermissions("order:view")
    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @Operation(summary = "审核订单")
    @RequiresPermissions("order:review")
    @PostMapping("/{id}/review")
    public ResponseEntity<Order> review(@PathVariable Long id, @RequestBody @Valid ReviewRequest request) {
        return ResponseEntity.ok(orderService.review(id, request.approved(), request.remark()));
    }

    @Operation(summary = "派单")
    @RequiresPermissions("order:assign")
    @PostMapping("/{id}/assign")
    public ResponseEntity<Order> assign(@PathVariable Long id, @RequestBody @Valid AssignRequest request) {
        return ResponseEntity.ok(orderService.assign(id, request.assigneeId(), request.assigneeName()));
    }

    @Operation(summary = "更新进度")
    @RequiresPermissions("order:update")
    @PostMapping("/{id}/progress")
    public ResponseEntity<OrderProgress> updateProgress(@PathVariable Long id, @RequestBody @Valid ProgressRequest request) {
        return ResponseEntity.ok(orderService.updateProgress(id, request.title(), request.content()));
    }

    @Operation(summary = "完成订单")
    @RequiresPermissions("order:update")
    @PostMapping("/{id}/complete")
    public ResponseEntity<Order> complete(@PathVariable Long id, @RequestBody CompleteRequest request) {
        return ResponseEntity.ok(orderService.complete(id, request.remark()));
    }

    @Operation(summary = "取消订单")
    @RequiresPermissions("order:update")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Order> cancel(@PathVariable Long id, @RequestBody @Valid CancelRequest request) {
        return ResponseEntity.ok(orderService.cancel(id, request.reason()));
    }

    @Operation(summary = "获取订单进度")
    @RequiresPermissions("order:view")
    @GetMapping("/{id}/progress")
    public ResponseEntity<List<OrderProgress>> getProgress(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getProgress(id));
    }

    @Operation(summary = "获取订单材料")
    @RequiresPermissions("order:view")
    @GetMapping("/{id}/materials")
    public ResponseEntity<List<OrderMaterial>> getMaterials(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getMaterials(id));
    }

    @Operation(summary = "上传材料")
    @RequiresPermissions("order:update")
    @PostMapping("/{id}/materials")
    public ResponseEntity<OrderMaterial> uploadMaterial(@PathVariable Long id, @RequestBody @Valid MaterialRequest request) {
        Order order = orderService.findById(id);
        OrderMaterial material = new OrderMaterial();
        material.setOrderId(id);
        material.setOrderNo(order.getOrderNo());
        material.setProgressId(request.progressId());
        material.setName(request.name());
        material.setMaterialType(request.materialType());
        material.setFileUrl(request.fileUrl());
        material.setFileType(request.fileType());
        material.setFileSize(request.fileSize());
        material.setUploadType("admin");
        material.setUploaderId(request.uploaderId());
        material.setUploaderName(request.uploaderName());
        material.setRemark(request.remark());
        return new ResponseEntity<>(orderService.uploadMaterial(material), HttpStatus.CREATED);
    }

    @Operation(summary = "删除材料")
    @RequiresPermissions("order:update")
    @DeleteMapping("/materials/{materialId}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long materialId) {
        orderService.deleteMaterial(materialId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "订单统计")
    @RequiresPermissions("order:view")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        // 获取当前用户
        UserinfoDTO userInfo = (UserinfoDTO) SessionItemHolder.getItem(Constants.SESSION_CURRENT_USER);
        Long assigneeId = null;
        
        // 判断是否是办理人员
        if (userInfo != null && userInfo.permissions() != null) {
            boolean hasAssignPermission = userInfo.permissions().contains("order:assign");
            boolean hasUpdatePermission = userInfo.permissions().contains("order:update");
            if (hasUpdatePermission && !hasAssignPermission) {
                assigneeId = userInfo.userId();
            }
        }
        
        return ResponseEntity.ok(orderService.getStats(assigneeId));
    }

    record ReviewRequest(@NotNull Boolean approved, String remark) {}
    record AssignRequest(@NotNull Long assigneeId, @NotBlank String assigneeName) {}
    record ProgressRequest(@NotBlank String title, String content) {}
    record CompleteRequest(String remark) {}
    record CancelRequest(@NotBlank String reason) {}
    record MaterialRequest(
        @NotBlank String name, String materialType, @NotBlank String fileUrl,
        String fileType, Long fileSize, Long progressId, Long uploaderId, String uploaderName, String remark
    ) {}
}
