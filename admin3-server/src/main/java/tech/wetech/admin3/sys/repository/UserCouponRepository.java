package tech.wetech.admin3.sys.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.UserCoupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    
    Page<UserCoupon> findByUserId(Long userId, Pageable pageable);
    
    Page<UserCoupon> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    
    List<UserCoupon> findByUserIdAndStatusOrderByEndTimeAsc(Long userId, String status);
    
    long countByUserIdAndCouponId(Long userId, Long couponId);
    
    long countByUserIdAndStatus(Long userId, String status);
    
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.userId = :userId AND uc.status = 'unused' " +
           "AND uc.startTime <= :now AND uc.endTime > :now " +
           "AND uc.minAmount <= :orderAmount " +
           "ORDER BY uc.value DESC")
    List<UserCoupon> findAvailableForOrder(Long userId, LocalDateTime now, BigDecimal orderAmount);
    
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.status = 'unused' AND uc.endTime < :now")
    List<UserCoupon> findExpiredCoupons(LocalDateTime now);
}
