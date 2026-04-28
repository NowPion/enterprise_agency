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
import tech.wetech.admin3.sys.model.Product;
import tech.wetech.admin3.sys.model.ProductField;
import tech.wetech.admin3.sys.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "产品管理", description = "业务产品管理接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "获取所有产品")
    @RequiresPermissions("product:view")
    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @Operation(summary = "获取产品详情")
    @RequiresPermissions("product:view")
    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(summary = "创建产品")
    @RequiresPermissions("product:create")
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody @Valid CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setCode(request.code());
        product.setCategory(request.category());
        product.setImage(request.image());
        product.setDescription(request.description());
        product.setOriginalPrice(request.originalPrice());
        product.setPrice(request.price());
        product.setProcessDays(request.processDays());
        product.setNotice(request.notice());
        product.setMaterials(request.materials());
        product.setSort(request.sort() != null ? request.sort() : 0);
        product.setStatus(request.status() != null ? request.status() : 1);
        return new ResponseEntity<>(productService.create(product), HttpStatus.CREATED);
    }

    @Operation(summary = "更新产品")
    @RequiresPermissions("product:update")
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setCode(request.code());
        product.setCategory(request.category());
        product.setImage(request.image());
        product.setDescription(request.description());
        product.setOriginalPrice(request.originalPrice());
        product.setPrice(request.price());
        product.setProcessDays(request.processDays());
        product.setNotice(request.notice());
        product.setMaterials(request.materials());
        product.setSort(request.sort());
        product.setStatus(request.status());
        return ResponseEntity.ok(productService.update(id, product));
    }

    @Operation(summary = "删除产品")
    @RequiresPermissions("product:delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 产品字段管理
    @Operation(summary = "获取产品字段")
    @RequiresPermissions("product:view")
    @GetMapping("/{id}/fields")
    public ResponseEntity<List<ProductField>> getFields(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findFieldsByProductId(id));
    }

    @Operation(summary = "创建产品字段")
    @RequiresPermissions("product:update")
    @PostMapping("/{id}/fields")
    public ResponseEntity<ProductField> createField(@PathVariable Long id, @RequestBody @Valid CreateFieldRequest request) {
        ProductField field = new ProductField();
        field.setProductId(id);
        field.setFieldName(request.fieldName());
        field.setFieldKey(request.fieldKey());
        field.setFieldType(request.fieldType());
        field.setPlaceholder(request.placeholder());
        field.setOptions(request.options());
        field.setIsRequired(request.isRequired() != null ? request.isRequired() : 0);
        field.setValidation(request.validation());
        field.setErrorMsg(request.errorMsg());
        field.setSort(request.sort() != null ? request.sort() : 0);
        field.setStatus(1);
        return new ResponseEntity<>(productService.createField(field), HttpStatus.CREATED);
    }

    @Operation(summary = "更新产品字段")
    @RequiresPermissions("product:update")
    @PutMapping("/fields/{fieldId}")
    public ResponseEntity<ProductField> updateField(@PathVariable Long fieldId, @RequestBody UpdateFieldRequest request) {
        ProductField field = new ProductField();
        field.setFieldName(request.fieldName());
        field.setFieldKey(request.fieldKey());
        field.setFieldType(request.fieldType());
        field.setPlaceholder(request.placeholder());
        field.setOptions(request.options());
        field.setIsRequired(request.isRequired());
        field.setValidation(request.validation());
        field.setErrorMsg(request.errorMsg());
        field.setSort(request.sort());
        field.setStatus(request.status());
        return ResponseEntity.ok(productService.updateField(fieldId, field));
    }

    @Operation(summary = "删除产品字段")
    @RequiresPermissions("product:update")
    @DeleteMapping("/fields/{fieldId}")
    public ResponseEntity<Void> deleteField(@PathVariable Long fieldId) {
        productService.deleteField(fieldId);
        return ResponseEntity.noContent().build();
    }

    // 产品年度价格管理
    @Operation(summary = "获取产品年度价格")
    @RequiresPermissions("product:view")
    @GetMapping("/{id}/year-prices")
    public ResponseEntity<List<?>> getYearPrices(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findYearPricesByProductId(id));
    }

    @Operation(summary = "创建产品年度价格")
    @RequiresPermissions("product:update")
    @PostMapping("/{id}/year-prices")
    public ResponseEntity<?> createYearPrice(@PathVariable Long id, @RequestBody @Valid CreateYearPriceRequest request) {
        return new ResponseEntity<>(productService.createYearPrice(id, request.year(), request.originalPrice(), request.price(), request.sort()), HttpStatus.CREATED);
    }

    @Operation(summary = "更新产品年度价格")
    @RequiresPermissions("product:update")
    @PutMapping("/year-prices/{priceId}")
    public ResponseEntity<?> updateYearPrice(@PathVariable Long priceId, @RequestBody UpdateYearPriceRequest request) {
        return ResponseEntity.ok(productService.updateYearPrice(priceId, request.year(), request.originalPrice(), request.price(), request.sort(), request.status()));
    }

    @Operation(summary = "删除产品年度价格")
    @RequiresPermissions("product:update")
    @DeleteMapping("/year-prices/{priceId}")
    public ResponseEntity<Void> deleteYearPrice(@PathVariable Long priceId) {
        productService.deleteYearPrice(priceId);
        return ResponseEntity.noContent().build();
    }

    record CreateProductRequest(
        @NotBlank String name, String code, String category, String image, String description,
        BigDecimal originalPrice, BigDecimal price, Integer processDays,
        String notice, String materials, Integer sort, Integer status
    ) {}

    record UpdateProductRequest(
        String name, String code, String category, String image, String description,
        BigDecimal originalPrice, BigDecimal price, Integer processDays,
        String notice, String materials, Integer sort, Integer status
    ) {}

    record CreateFieldRequest(
        @NotBlank String fieldName, @NotBlank String fieldKey, String fieldType,
        String placeholder, String options, Integer isRequired,
        String validation, String errorMsg, Integer sort
    ) {}

    record UpdateFieldRequest(
        String fieldName, String fieldKey, String fieldType,
        String placeholder, String options, Integer isRequired,
        String validation, String errorMsg, Integer sort, Integer status
    ) {}

    record CreateYearPriceRequest(
        @NotBlank String year, BigDecimal originalPrice, BigDecimal price, Integer sort
    ) {}

    record UpdateYearPriceRequest(
        String year, BigDecimal originalPrice, BigDecimal price, Integer sort, Integer status
    ) {}
}
