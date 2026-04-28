package tech.wetech.admin3.sys.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNo(String orderNo);
    List<Order> findByUserIdOrderByCreateTimeDesc(Long userId);
    List<Order> findByUserIdAndStatusOrderByCreateTimeDesc(Long userId, String status);
    Page<Order> findByStatus(String status, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE (:status IS NULL OR :status = '' OR o.status = :status) " +
           "AND (:keyword IS NULL OR :keyword = '' OR o.orderNo LIKE %:keyword% OR o.companyName LIKE %:keyword%) " +
           "AND (:assigneeId IS NULL OR o.assigneeId = :assigneeId) " +
           "AND (:userId IS NULL OR o.userId = :userId)")
    Page<Order> search(@Param("keyword") String keyword, @Param("status") String status, 
                       @Param("assigneeId") Long assigneeId, @Param("userId") Long userId, Pageable pageable);
    
    long countByStatus(String status);
    long countByAssigneeId(Long assigneeId);
    long countByAssigneeIdAndStatus(Long assigneeId, String status);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, String status);
    
    // 小程序端分页查询
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByUserIdAndStatusOrderByCreateTimeDesc(Long userId, String status, Pageable pageable);
}
