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
import tech.wetech.admin3.sys.model.Notice;
import tech.wetech.admin3.sys.service.NoticeService;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "公告管理", description = "公告管理接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Operation(summary = "获取所有公告")
    @RequiresPermissions("notice:view")
    @GetMapping
    public ResponseEntity<List<Notice>> findAll() {
        return ResponseEntity.ok(noticeService.findAll());
    }

    @Operation(summary = "获取公告详情")
    @RequiresPermissions("notice:view")
    @GetMapping("/{id}")
    public ResponseEntity<Notice> findById(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.findById(id));
    }

    @Operation(summary = "创建公告")
    @RequiresPermissions("notice:create")
    @PostMapping
    public ResponseEntity<Notice> create(@RequestBody @Valid CreateNoticeRequest request) {
        Notice notice = new Notice();
        notice.setTitle(request.title());
        notice.setContent(request.content());
        notice.setNoticeType(request.noticeType() != null ? request.noticeType() : "system");
        notice.setIsTop(request.isTop() != null ? request.isTop() : 0);
        notice.setSort(request.sort() != null ? request.sort() : 0);
        notice.setStatus(request.status() != null ? request.status() : 1);
        notice.setPublishTime(request.publishTime() != null ? request.publishTime() : LocalDateTime.now());
        return new ResponseEntity<>(noticeService.create(notice), HttpStatus.CREATED);
    }

    @Operation(summary = "更新公告")
    @RequiresPermissions("notice:update")
    @PutMapping("/{id}")
    public ResponseEntity<Notice> update(@PathVariable Long id, @RequestBody UpdateNoticeRequest request) {
        Notice notice = new Notice();
        notice.setTitle(request.title());
        notice.setContent(request.content());
        notice.setNoticeType(request.noticeType());
        notice.setIsTop(request.isTop());
        notice.setSort(request.sort());
        notice.setStatus(request.status());
        notice.setPublishTime(request.publishTime());
        return ResponseEntity.ok(noticeService.update(id, notice));
    }

    @Operation(summary = "删除公告")
    @RequiresPermissions("notice:delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    record CreateNoticeRequest(
        @NotBlank String title, String content, String noticeType,
        Integer isTop, Integer sort, Integer status, LocalDateTime publishTime
    ) {}

    record UpdateNoticeRequest(
        String title, String content, String noticeType,
        Integer isTop, Integer sort, Integer status, LocalDateTime publishTime
    ) {}
}
