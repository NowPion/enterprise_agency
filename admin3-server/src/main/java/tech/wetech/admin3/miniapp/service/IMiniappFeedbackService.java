package tech.wetech.admin3.miniapp.service;

import tech.wetech.admin3.miniapp.dto.SubmitFeedbackDTO;
import tech.wetech.admin3.miniapp.vo.FeedbackVO;
import tech.wetech.admin3.miniapp.vo.PageVO;

/**
 * 小程序反馈服务接口
 */
public interface IMiniappFeedbackService {
    
    /**
     * 提交反馈
     */
    FeedbackVO submitFeedback(Long userId, SubmitFeedbackDTO dto);
    
    /**
     * 获取反馈列表
     */
    PageVO<FeedbackVO> getFeedbackList(Long userId, int page, int size);
    
    /**
     * 获取反馈详情
     */
    FeedbackVO getFeedbackDetail(Long userId, Long feedbackId);
}
