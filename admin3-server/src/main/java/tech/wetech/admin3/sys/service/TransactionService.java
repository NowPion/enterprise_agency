package tech.wetech.admin3.sys.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.Transaction;
import tech.wetech.admin3.sys.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Page<Transaction> findAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public Page<Transaction> findByType(String transactionType, Pageable pageable) {
        return transactionRepository.findByTransactionType(transactionType, pageable);
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "交易记录不存在"));
    }

    public Transaction findByTransactionNo(String transactionNo) {
        return transactionRepository.findByTransactionNo(transactionNo)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "交易记录不存在"));
    }

    public List<Transaction> findByOrderId(Long orderId) {
        return transactionRepository.findByOrderIdOrderByCreateTimeDesc(orderId);
    }

    // 统计
    public BigDecimal sumPayAmount() {
        return transactionRepository.findAll().stream()
            .filter(t -> "pay".equals(t.getTransactionType()) && "success".equals(t.getStatus()))
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal sumRefundAmount() {
        return transactionRepository.findAll().stream()
            .filter(t -> "refund".equals(t.getTransactionType()) && "success".equals(t.getStatus()))
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long countByStatus(String status) {
        return transactionRepository.findAll().stream()
            .filter(t -> status.equals(t.getStatus()))
            .count();
    }
}
