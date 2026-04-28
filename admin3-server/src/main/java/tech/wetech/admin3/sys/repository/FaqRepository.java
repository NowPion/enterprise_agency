package tech.wetech.admin3.sys.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.Faq;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByStatusOrderBySortDescCreateTimeDesc(Integer status);
    List<Faq> findByCategoryAndStatusOrderBySortDesc(String category, Integer status);
    Page<Faq> findByStatus(Integer status, Pageable pageable);
    Page<Faq> findByCategoryAndStatus(String category, Integer status, Pageable pageable);
}
