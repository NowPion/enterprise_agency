package tech.wetech.admin3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.common.authz.RequiresPermissions;
import tech.wetech.admin3.sys.model.MiniappUser;
import tech.wetech.admin3.sys.service.MiniappUserService;

@Tag(name = "小程序用户", description = "小程序用户管理接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/miniapp-users")
public class MiniappUserController {

    private final MiniappUserService userService;

    public MiniappUserController(MiniappUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "分页查询用户")
    @RequiresPermissions("miniapp-user:view")
    @GetMapping
    public ResponseEntity<Page<MiniappUser>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.search(keyword, status, page, size));
    }

    @Operation(summary = "获取用户详情")
    @RequiresPermissions("miniapp-user:view")
    @GetMapping("/{id}")
    public ResponseEntity<MiniappUser> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "更新用户信息")
    @RequiresPermissions("miniapp-user:update")
    @PutMapping("/{id}")
    public ResponseEntity<MiniappUser> update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(id, request.nickname(), request.phone(), request.status()));
    }

    @Operation(summary = "更新用户状态")
    @RequiresPermissions("miniapp-user:update")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "拉黑用户")
    @RequiresPermissions("miniapp-user:update")
    @PostMapping("/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable Long id, @RequestBody BlockUserRequest request) {
        userService.blockUser(id, request.reason());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "解除拉黑")
    @RequiresPermissions("miniapp-user:update")
    @PostMapping("/{id}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable Long id) {
        userService.unblockUser(id);
        return ResponseEntity.ok().build();
    }

    record UpdateUserRequest(String nickname, String phone, Integer status) {}
    record BlockUserRequest(String reason) {}
}
