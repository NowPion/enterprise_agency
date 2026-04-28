package tech.wetech.admin3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.common.authz.RequiresPermissions;
import tech.wetech.admin3.sys.model.Coupon;
import tech.wetech.admin3.sys.service.CouponService;

/**
 * 后台优惠券管理
 */
@Tag(name = "优惠券管理")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/coupon")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "优惠券列表")
    @RequiresPermissions("coupon:view")
    @GetMapping
    public ResponseEntity<Page<Coupon>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(couponService.search(keyword, status, page, size));
    }

    @Operation(summary = "优惠券详情")
    @RequiresPermissions("coupon:view")
    @GetMapping("/{id}")
    public ResponseEntity<Coupon> detail(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.getById(id));
    }

    @Operation(summary = "创建优惠券")
    @RequiresPermissions("coupon:create")
    @PostMapping
    public ResponseEntity<Coupon> create(@RequestBody Coupon coupon) {
        return new ResponseEntity<>(couponService.create(coupon), HttpStatus.CREATED);
    }

    @Operation(summary = "更新优惠券")
    @RequiresPermissions("coupon:update")
    @PutMapping("/{id}")
    public ResponseEntity<Coupon> update(@PathVariable Long id, @RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.update(id, coupon));
    }

    @Operation(summary = "更新优惠券状态")
    @RequiresPermissions("coupon:update")
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        couponService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "删除优惠券")
    @RequiresPermissions("coupon:delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        couponService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
