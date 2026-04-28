package tech.wetech.admin3.miniapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.miniapp.ApiResult;
import tech.wetech.admin3.miniapp.auth.MiniappAuth;
import tech.wetech.admin3.miniapp.dto.SubmitFeedbackDTO;
import tech.wetech.admin3.miniapp.service.IMiniappFeedbackService;
import tech.wetech.admin3.miniapp.vo.FeedbackVO;
import tech.wetech.admin3.miniapp.vo.PageVO;

/**
 * 小程序反馈接口
 */
@Tag(name = "小程序-反馈", description = "小程序反馈相关接口")
@RestController("miniappFeedbackController")
@RequestMapping("/minapp/feedback")
public class FeedbackController {

    private final IMiniappFeedbackService feedbackService;

    public FeedbackController(IMiniappFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Operation(summary = "提交反馈")
    @PostMapping("/submit")
    public ApiResult<FeedbackVO> submitFeedback(@MiniappAuth Long userId, @RequestBody SubmitFeedbackDTO dto) {
        return ApiResult.ok(feedbackService.submitFeedback(userId, dto));
    }

    @Operation(summary = "获取我的反馈列表")
    @GetMapping("/list")
    public ApiResult<PageVO<FeedbackVO>> getFeedbackList(
            @MiniappAuth Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResult.ok(feedbackService.getFeedbackList(userId, page, size));
    }

    @Operation(summary = "获取反馈详情")
    @GetMapping("/detail/{id}")
    public ApiResult<FeedbackVO> getFeedbackDetail(@MiniappAuth Long userId, @PathVariable Long id) {
        return ApiResult.ok(feedbackService.getFeedbackDetail(userId, id));
    }
}
