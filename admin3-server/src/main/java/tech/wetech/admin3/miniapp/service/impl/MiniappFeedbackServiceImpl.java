package tech.wetech.admin3.miniapp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.miniapp.dto.SubmitFeedbackDTO;
import tech.wetech.admin3.miniapp.service.IMiniappFeedbackService;
import tech.wetech.admin3.miniapp.vo.FeedbackVO;
import tech.wetech.admin3.miniapp.vo.PageVO;
import tech.wetech.admin3.sys.model.Feedback;
import tech.wetech.admin3.sys.repository.FeedbackRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MiniappFeedbackServiceImpl implements IMiniappFeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ObjectMapper objectMapper;

    public MiniappFeedbackServiceImpl(FeedbackRepository feedbackRepository, ObjectMapper objectMapper) {
        this.feedbackRepository = feedbackRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public FeedbackVO submitFeedback(Long userId, SubmitFeedbackDTO dto) {
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "反馈内容不能为空");
        }
        
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setType(dto.getType() != null ? dto.getType() : "other");
        feedback.setContent(dto.getContent());
        feedback.setContact(dto.getContact());
        
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            try {
                feedback.setImages(objectMapper.writeValueAsString(dto.getImages()));
            } catch (JsonProcessingException e) {
                feedback.setImages("[]");
            }
        }
        
        feedback = feedbackRepository.save(feedback);
        return convertToVO(feedback);
    }

    @Override
    public PageVO<FeedbackVO> getFeedbackList(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Feedback> feedbackPage = feedbackRepository.findByUserId(userId, pageRequest);
        
        List<FeedbackVO> list = feedbackPage.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return new PageVO<>(list, feedbackPage.getTotalElements());
    }

    @Override
    public FeedbackVO getFeedbackDetail(Long userId, Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "反馈不存在"));
        
        if (!feedback.getUserId().equals(userId)) {
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无权查看此反馈");
        }
        
        return convertToVO(feedback);
    }

    private FeedbackVO convertToVO(Feedback feedback) {
        FeedbackVO vo = new FeedbackVO();
        vo.setId(feedback.getId());
        vo.setType(feedback.getType());
        vo.setTypeText(getTypeText(feedback.getType()));
        vo.setContent(feedback.getContent());
        vo.setContact(feedback.getContact());
        vo.setStatus(feedback.getStatus());
        vo.setStatusText(getStatusText(feedback.getStatus()));
        vo.setReply(feedback.getReply());
        vo.setReplyTime(feedback.getReplyTime());
        vo.setCreateTime(feedback.getCreateTime());
        
        if (feedback.getImages() != null && !feedback.getImages().isEmpty()) {
            try {
                vo.setImages(objectMapper.readValue(feedback.getImages(), new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException e) {
                vo.setImages(new ArrayList<>());
            }
        } else {
            vo.setImages(new ArrayList<>());
        }
        
        return vo;
    }

    private String getTypeText(String type) {
        return switch (type) {
            case "suggestion" -> "建议";
            case "complaint" -> "投诉";
            case "question" -> "问题";
            default -> "其他";
        };
    }

    private String getStatusText(String status) {
        return switch (status) {
            case "pending" -> "待处理";
            case "processing" -> "处理中";
            case "resolved" -> "已解决";
            case "closed" -> "已关闭";
            default -> "未知";
        };
    }
}
