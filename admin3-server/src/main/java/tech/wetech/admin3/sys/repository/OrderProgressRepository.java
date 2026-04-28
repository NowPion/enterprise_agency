package tech.wetech.admin3.sys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.OrderProgress;

import java.util.List;

@Repository
public interface OrderProgressRepository extends JpaRepository<OrderProgress, Long> {
    List<OrderProgress> findByOrderIdOrderByCreateTimeDesc(Long orderId);
    List<OrderProgress> findByOrderNoOrderByCreateTimeDesc(String orderNo);
}
