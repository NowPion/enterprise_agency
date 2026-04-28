package tech.wetech.admin3.sys.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.wetech.admin3.sys.model.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT f FROM Feedback f WHERE (:status IS NULL OR f.status = :status) ORDER BY f.createTime DESC")
    Page<Feedback> findByStatus(@Param("status") String status, Pageable pageable);

    Page<Feedback> findByUserId(Long userId, Pageable pageable);

    long countByStatus(String status);
}
