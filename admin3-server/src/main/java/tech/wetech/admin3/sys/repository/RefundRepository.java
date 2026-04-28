package tech.wetech.admin3.sys.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.Refund;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findByRefundNo(String refundNo);
    Optional<Refund> findByWechatRefundId(String wechatRefundId);
    List<Refund> findByOrderIdOrderByCreateTimeDesc(Long orderId);
    Page<Refund> findByStatus(String status, Pageable pageable);
    boolean existsByOrderIdAndStatusIn(Long orderId, Collection<String> statuses);
}
