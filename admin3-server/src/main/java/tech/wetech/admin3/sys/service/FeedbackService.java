package tech.wetech.admin3.sys.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.Feedback;
import tech.wetech.admin3.sys.repository.FeedbackRepository;

import java.time.LocalDateTime;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public Page<Feedback> findAll(String status, int page, int size) {
        return feedbackRepository.findByStatus(status, PageRequest.of(page - 1, size));
    }

    public Feedback findById(Long id) {
        return feedbackRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST));
    }

    @Transactional
    public Feedback reply(Long id, String reply) {
        Feedback feedback = findById(id);
        feedback.setReply(reply);
        feedback.setReplyTime(LocalDateTime.now());
        feedback.setStatus("resolved");
        return feedbackRepository.save(feedback);
    }

    @Transactional
    public Feedback updateStatus(Long id, String status) {
        Feedback feedback = findById(id);
        feedback.setStatus(status);
        return feedbackRepository.save(feedback);
    }

    @Transactional
    public void delete(Long id) {
        feedbackRepository.deleteById(id);
    }

    public long countPending() {
        return feedbackRepository.countByStatus("pending");
    }
}
