package tech.wetech.admin3.sys.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.Notice;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByStatusOrderByIsTopDescSortDescCreateTimeDesc(Integer status);
    Page<Notice> findByStatusOrderByIsTopDescSortDescCreateTimeDesc(Integer status, Pageable pageable);
    Page<Notice> findByStatus(Integer status, Pageable pageable);
}
