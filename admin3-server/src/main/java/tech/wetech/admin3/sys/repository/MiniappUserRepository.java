package tech.wetech.admin3.sys.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.wetech.admin3.sys.model.MiniappUser;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MiniappUserRepository extends JpaRepository<MiniappUser, Long> {

    Optional<MiniappUser> findByOpenid(String openid);

    Optional<MiniappUser> findByPhone(String phone);

    @Query("SELECT u FROM MiniappUser u WHERE " +
           "(:keyword IS NULL OR u.nickname LIKE %:keyword% OR u.phone LIKE %:keyword%) " +
           "AND (:status IS NULL OR u.status = :status)")
    Page<MiniappUser> search(String keyword, Integer status, Pageable pageable);

    long countByCreateTimeAfter(LocalDateTime time);

    long countByCreateTimeBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByPhoneAndBlocked(String phone, Integer blocked);
}
