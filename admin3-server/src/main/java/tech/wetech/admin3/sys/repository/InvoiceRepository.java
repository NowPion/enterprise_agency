package tech.wetech.admin3.sys.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.Invoice;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNo(String invoiceNo);
    Optional<Invoice> findByOrderId(Long orderId);
    List<Invoice> findByUserIdOrderByCreateTimeDesc(Long userId);
    Page<Invoice> findByStatus(String status, Pageable pageable);
    Page<Invoice> findByUserId(Long userId, Pageable pageable);
    boolean existsByOrderId(Long orderId);
}
