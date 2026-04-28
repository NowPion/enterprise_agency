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
import tech.wetech.admin3.sys.model.Banner;
import tech.wetech.admin3.sys.service.BannerService;

import java.util.List;

@Tag(name = "轮播图管理", description = "轮播图管理接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/banners")
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @Operation(summary = "获取所有轮播图")
    @RequiresPermissions("banner:view")
    @GetMapping
    public ResponseEntity<List<Banner>> findAll() {
        return ResponseEntity.ok(bannerService.findAll());
    }

    @Operation(summary = "获取启用的轮播图")
    @GetMapping("/enabled")
    public ResponseEntity<List<Banner>> findEnabled() {
        return ResponseEntity.ok(bannerService.findEnabled());
    }

    @Operation(summary = "获取轮播图详情")
    @GetMapping("/{id}")
    public ResponseEntity<Banner> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bannerService.findById(id));
    }

    @Operation(summary = "创建轮播图")
    @RequiresPermissions("banner:create")
    @PostMapping
    public ResponseEntity<Banner> create(@RequestBody @Valid CreateBannerRequest request) {
        Banner banner = new Banner();
        banner.setTitle(request.title());
        banner.setImage(request.image());
        banner.setLinkType(request.linkType() != null ? request.linkType() : 0);
        banner.setLinkUrl(request.linkUrl());
        banner.setSort(request.sort() != null ? request.sort() : 0);
        banner.setStatus(request.status() != null ? request.status() : 1);
        return new ResponseEntity<>(bannerService.create(banner), HttpStatus.CREATED);
    }

    @Operation(summary = "更新轮播图")
    @RequiresPermissions("banner:update")
    @PutMapping("/{id}")
    public ResponseEntity<Banner> update(@PathVariable Long id, @RequestBody UpdateBannerRequest request) {
        Banner banner = new Banner();
        banner.setTitle(request.title());
        banner.setImage(request.image());
        banner.setLinkType(request.linkType());
        banner.setLinkUrl(request.linkUrl());
        banner.setSort(request.sort());
        banner.setStatus(request.status());
        return ResponseEntity.ok(bannerService.update(id, banner));
    }

    @Operation(summary = "删除轮播图")
    @RequiresPermissions("banner:delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bannerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    record CreateBannerRequest(
        String title,
        @NotBlank String image,
        Integer linkType,
        String linkUrl,
        Integer sort,
        Integer status
    ) {}

    record UpdateBannerRequest(
        String title,
        String image,
        Integer linkType,
        String linkUrl,
        Integer sort,
        Integer status
    ) {}
}
