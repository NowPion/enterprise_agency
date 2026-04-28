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
import tech.wetech.admin3.sys.model.Faq;
import tech.wetech.admin3.sys.service.FaqService;

import java.util.List;

@Tag(name = "常见问题管理", description = "FAQ管理接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/faqs")
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @Operation(summary = "获取所有FAQ")
    @RequiresPermissions("faq:view")
    @GetMapping
    public ResponseEntity<List<Faq>> findAll() {
        return ResponseEntity.ok(faqService.findAll());
    }

    @Operation(summary = "获取FAQ详情")
    @RequiresPermissions("faq:view")
    @GetMapping("/{id}")
    public ResponseEntity<Faq> findById(@PathVariable Long id) {
        return ResponseEntity.ok(faqService.findById(id));
    }

    @Operation(summary = "创建FAQ")
    @RequiresPermissions("faq:create")
    @PostMapping
    public ResponseEntity<Faq> create(@RequestBody @Valid CreateFaqRequest request) {
        Faq faq = new Faq();
        faq.setQuestion(request.question());
        faq.setAnswer(request.answer());
        faq.setCategory(request.category() != null ? request.category() : "general");
        faq.setSort(request.sort() != null ? request.sort() : 0);
        faq.setStatus(request.status() != null ? request.status() : 1);
        return new ResponseEntity<>(faqService.create(faq), HttpStatus.CREATED);
    }

    @Operation(summary = "更新FAQ")
    @RequiresPermissions("faq:update")
    @PutMapping("/{id}")
    public ResponseEntity<Faq> update(@PathVariable Long id, @RequestBody UpdateFaqRequest request) {
        Faq faq = new Faq();
        faq.setQuestion(request.question());
        faq.setAnswer(request.answer());
        faq.setCategory(request.category());
        faq.setSort(request.sort());
        faq.setStatus(request.status());
        return ResponseEntity.ok(faqService.update(id, faq));
    }

    @Operation(summary = "删除FAQ")
    @RequiresPermissions("faq:delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        faqService.delete(id);
        return ResponseEntity.noContent().build();
    }

    record CreateFaqRequest(
        @NotBlank String question, String answer, String category, Integer sort, Integer status
    ) {}

    record UpdateFaqRequest(
        String question, String answer, String category, Integer sort, Integer status
    ) {}
}
