package tech.wetech.admin3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.ProductCategory;
import tech.wetech.admin3.sys.repository.ProductCategoryRepository;

import java.util.List;

@Tag(name = "产品分类管理")
@RestController
@RequestMapping("/admin/product-category")
public class ProductCategoryController {

    private final ProductCategoryRepository categoryRepository;

    public ProductCategoryController(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Operation(summary = "获取所有分类")
    @GetMapping
    public ResponseEntity<List<ProductCategory>> list() {
        return ResponseEntity.ok(categoryRepository.findAllByOrderBySortDesc());
    }

    @Operation(summary = "获取启用的分类")
    @GetMapping("/enabled")
    public ResponseEntity<List<ProductCategory>> listEnabled() {
        return ResponseEntity.ok(categoryRepository.findByStatusOrderBySortDesc(1));
    }

    @Operation(summary = "创建分类")
    @PostMapping
    public ResponseEntity<ProductCategory> create(@RequestBody ProductCategory category) {
        if (categoryRepository.existsByCode(category.getCode())) {
            throw new BusinessException(CommonResultStatus.FAIL, "分类编码已存在");
        }
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public ResponseEntity<ProductCategory> update(@PathVariable Long id, @RequestBody ProductCategory category) {
        ProductCategory existing = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "分类不存在"));
        
        // 检查编码是否被其他分类使用
        categoryRepository.findByCode(category.getCode()).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new BusinessException(CommonResultStatus.FAIL, "分类编码已存在");
            }
        });
        
        existing.setName(category.getName());
        existing.setCode(category.getCode());
        existing.setIcon(category.getIcon());
        existing.setSort(category.getSort());
        existing.setStatus(category.getStatus());
        return ResponseEntity.ok(categoryRepository.save(existing));
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
