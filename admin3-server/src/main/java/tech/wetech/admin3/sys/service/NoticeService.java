package tech.wetech.admin3.sys.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.Notice;
import tech.wetech.admin3.sys.repository.NoticeRepository;

import java.util.List;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<Notice> findAll() {
        return noticeRepository.findAll();
    }

    public List<Notice> findEnabled() {
        return noticeRepository.findByStatusOrderByIsTopDescSortDescCreateTimeDesc(1);
    }

    public Page<Notice> findEnabled(Pageable pageable) {
        return noticeRepository.findByStatusOrderByIsTopDescSortDescCreateTimeDesc(1, pageable);
    }

    public Notice findById(Long id) {
        return noticeRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "公告不存在"));
    }

    @Transactional
    public Notice create(Notice notice) {
        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice update(Long id, Notice notice) {
        Notice existing = findById(id);
        if (notice.getTitle() != null) existing.setTitle(notice.getTitle());
        if (notice.getContent() != null) existing.setContent(notice.getContent());
        if (notice.getNoticeType() != null) existing.setNoticeType(notice.getNoticeType());
        if (notice.getIsTop() != null) existing.setIsTop(notice.getIsTop());
        if (notice.getSort() != null) existing.setSort(notice.getSort());
        if (notice.getStatus() != null) existing.setStatus(notice.getStatus());
        if (notice.getPublishTime() != null) existing.setPublishTime(notice.getPublishTime());
        return noticeRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }
}
