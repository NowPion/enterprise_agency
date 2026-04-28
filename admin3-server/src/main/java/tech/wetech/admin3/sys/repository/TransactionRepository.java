package tech.wetech.admin3.sys.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.Transaction;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionNo(String transactionNo);
    List<Transaction> findByOrderIdOrderByCreateTimeDesc(Long orderId);
    List<Transaction> findByUserIdOrderByCreateTimeDesc(Long userId);
    Page<Transaction> findByTransactionType(String transactionType, Pageable pageable);
}
