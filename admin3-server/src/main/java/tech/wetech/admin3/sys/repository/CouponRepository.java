package tech.wetech.admin3.sys.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.Coupon;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    Page<Coupon> findByStatus(Integer status, Pageable pageable);
    
    @Query("SELECT c FROM Coupon c WHERE c.status = 1 " +
           "AND (c.totalCount = -1 OR c.receivedCount < c.totalCount) " +
           "AND (c.endTime IS NULL OR c.endTime > :now)")
    List<Coupon> findAvailableCoupons(LocalDateTime now);
    
    @Query("SELECT c FROM Coupon c WHERE c.status = :status " +
           "AND (:keyword IS NULL OR c.name LIKE %:keyword%)")
    Page<Coupon> search(String keyword, Integer status, Pageable pageable);
}
