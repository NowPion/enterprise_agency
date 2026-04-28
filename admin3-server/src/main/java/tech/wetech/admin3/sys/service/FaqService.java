package tech.wetech.admin3.sys.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.Faq;
import tech.wetech.admin3.sys.repository.FaqRepository;

import java.util.List;

@Service
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<Faq> findAll() {
        return faqRepository.findAll();
    }

    public List<Faq> findEnabled() {
        return faqRepository.findByStatusOrderBySortDescCreateTimeDesc(1);
    }

    public List<Faq> findByCategory(String category) {
        return faqRepository.findByCategoryAndStatusOrderBySortDesc(category, 1);
    }

    public Faq findById(Long id) {
        return faqRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "FAQ不存在"));
    }

    @Transactional
    public Faq create(Faq faq) {
        return faqRepository.save(faq);
    }

    @Transactional
    public Faq update(Long id, Faq faq) {
        Faq existing = findById(id);
        if (faq.getQuestion() != null) existing.setQuestion(faq.getQuestion());
        if (faq.getAnswer() != null) existing.setAnswer(faq.getAnswer());
        if (faq.getCategory() != null) existing.setCategory(faq.getCategory());
        if (faq.getSort() != null) existing.setSort(faq.getSort());
        if (faq.getStatus() != null) existing.setStatus(faq.getStatus());
        return faqRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        faqRepository.deleteById(id);
    }

    @Transactional
    public void incrementViewCount(Long id) {
        Faq faq = findById(id);
        faq.setViewCount(faq.getViewCount() + 1);
        faqRepository.save(faq);
    }
}
