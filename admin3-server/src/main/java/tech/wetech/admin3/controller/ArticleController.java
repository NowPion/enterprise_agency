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
import tech.wetech.admin3.sys.model.Article;
import tech.wetech.admin3.sys.service.ArticleService;

import java.util.List;

@Tag(name = "文章管理", description = "文章管理接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Operation(summary = "获取所有文章")
    @RequiresPermissions("article:view")
    @GetMapping
    public ResponseEntity<List<Article>> findAll() {
        return ResponseEntity.ok(articleService.findAll());
    }

    @Operation(summary = "获取文章详情")
    @RequiresPermissions("article:view")
    @GetMapping("/{id}")
    public ResponseEntity<Article> findById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.findById(id));
    }

    @Operation(summary = "创建文章")
    @RequiresPermissions("article:create")
    @PostMapping
    public ResponseEntity<Article> create(@RequestBody @Valid CreateArticleRequest request) {
        Article article = new Article();
        article.setTitle(request.title());
        article.setContent(request.content());
        article.setCover(request.cover());
        article.setSummary(request.summary());
        article.setArticleType(request.articleType() != null ? request.articleType() : "guide");
        article.setCategory(request.category());
        article.setSort(request.sort() != null ? request.sort() : 0);
        article.setStatus(request.status() != null ? request.status() : 1);
        return new ResponseEntity<>(articleService.create(article), HttpStatus.CREATED);
    }

    @Operation(summary = "更新文章")
    @RequiresPermissions("article:update")
    @PutMapping("/{id}")
    public ResponseEntity<Article> update(@PathVariable Long id, @RequestBody UpdateArticleRequest request) {
        Article article = new Article();
        article.setTitle(request.title());
        article.setContent(request.content());
        article.setCover(request.cover());
        article.setSummary(request.summary());
        article.setArticleType(request.articleType());
        article.setCategory(request.category());
        article.setSort(request.sort());
        article.setStatus(request.status());
        return ResponseEntity.ok(articleService.update(id, article));
    }

    @Operation(summary = "删除文章")
    @RequiresPermissions("article:delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    record CreateArticleRequest(
        @NotBlank String title, String content, String cover, String summary,
        String articleType, String category, Integer sort, Integer status
    ) {}

    record UpdateArticleRequest(
        String title, String content, String cover, String summary,
        String articleType, String category, Integer sort, Integer status
    ) {}
}
