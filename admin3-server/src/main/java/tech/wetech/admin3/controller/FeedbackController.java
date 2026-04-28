package tech.wetech.admin3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.sys.model.Feedback;
import tech.wetech.admin3.sys.service.FeedbackService;

import java.util.Map;

@Tag(name = "反馈管理", description = "用户反馈管理接口")
@RestController
@RequestMapping("/admin/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Operation(summary = "获取反馈列表")
    @GetMapping
    public ResponseEntity<Page<Feedback>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(feedbackService.findAll(status, page, size));
    }

    @Operation(summary = "获取反馈详情")
    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.findById(id));
    }

    @Operation(summary = "回复反馈")
    @PostMapping("/{id}/reply")
    public ResponseEntity<Feedback> reply(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(feedbackService.reply(id, body.get("reply")));
    }

    @Operation(summary = "更新状态")
    @PutMapping("/{id}/status")
    public ResponseEntity<Feedback> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(feedbackService.updateStatus(id, body.get("status")));
    }

    @Operation(summary = "删除反馈")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feedbackService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "获取待处理数量")
    @GetMapping("/pending-count")
    public ResponseEntity<Map<String, Long>> getPendingCount() {
        return ResponseEntity.ok(Map.of("count", feedbackService.countPending()));
    }
}
