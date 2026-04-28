package tech.wetech.admin3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.common.authz.RequiresPermissions;
import tech.wetech.admin3.sys.model.SystemConfig;
import tech.wetech.admin3.sys.service.SystemConfigService;

import java.util.List;
import java.util.Map;

/**
 * 系统配置管理
 *
 * @author cjbi
 */
@Tag(name = "系统配置", description = "系统配置管理接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/system-configs")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    public SystemConfigController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    @Operation(summary = "获取所有配置")
    @RequiresPermissions("config:view")
    @GetMapping
    public ResponseEntity<List<SystemConfig>> findAll() {
        return ResponseEntity.ok(systemConfigService.findAll());
    }

    @Operation(summary = "获取配置Map")
    @RequiresPermissions("config:view")
    @GetMapping("/map")
    public ResponseEntity<Map<String, String>> getConfigMap() {
        return ResponseEntity.ok(systemConfigService.getConfigMap());
    }

    @Operation(summary = "根据ID获取配置")
    @RequiresPermissions("config:view")
    @GetMapping("/{id}")
    public ResponseEntity<SystemConfig> findById(@PathVariable Long id) {
        return ResponseEntity.ok(systemConfigService.findById(id));
    }

    @Operation(summary = "根据Key获取配置")
    @RequiresPermissions("config:view")
    @GetMapping("/key/{key}")
    public ResponseEntity<SystemConfig> findByKey(@PathVariable String key) {
        return ResponseEntity.ok(systemConfigService.findByKey(key));
    }

    @Operation(summary = "根据前缀获取配置")
    @RequiresPermissions("config:view")
    @GetMapping("/prefix/{prefix}")
    public ResponseEntity<List<SystemConfig>> findByPrefix(@PathVariable String prefix) {
        return ResponseEntity.ok(systemConfigService.findByKeyPrefix(prefix));
    }

    @Operation(summary = "创建配置")
    @RequiresPermissions("config:create")
    @PostMapping
    public ResponseEntity<SystemConfig> create(@RequestBody @Valid CreateConfigRequest request) {
        SystemConfig config = systemConfigService.create(
            request.configKey(),
            request.configValue(),
            request.configType(),
            request.description()
        );
        return new ResponseEntity<>(config, HttpStatus.CREATED);
    }

    @Operation(summary = "更新配置")
    @RequiresPermissions("config:update")
    @PutMapping("/{id}")
    public ResponseEntity<SystemConfig> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateConfigRequest request) {
        SystemConfig config = systemConfigService.update(
            id,
            request.configValue(),
            request.configType(),
            request.description()
        );
        return ResponseEntity.ok(config);
    }

    @Operation(summary = "批量更新配置")
    @RequiresPermissions("config:update")
    @PutMapping("/batch")
    public ResponseEntity<Void> batchUpdate(@RequestBody List<SystemConfig> configs) {
        systemConfigService.batchUpdate(configs);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "根据Key更新配置值")
    @RequiresPermissions("config:update")
    @PutMapping("/key/{key}")
    public ResponseEntity<Void> updateByKey(
            @PathVariable String key,
            @RequestBody UpdateByKeyRequest request) {
        systemConfigService.updateByKey(key, request.configValue());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "删除配置")
    @RequiresPermissions("config:delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        systemConfigService.delete(id);
        return ResponseEntity.noContent().build();
    }

    record CreateConfigRequest(
        @NotBlank String configKey,
        String configValue,
        String configType,
        String description
    ) {}

    record UpdateConfigRequest(
        String configValue,
        String configType,
        String description
    ) {}

    record UpdateByKeyRequest(String configValue) {}
}
