package tech.wetech.admin3.sys.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.Invoice;
import tech.wetech.admin3.sys.repository.InvoiceRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private static final AtomicLong counter = new AtomicLong(0);

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public Page<Invoice> findAll(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    public Page<Invoice> findByStatus(String status, Pageable pageable) {
        return invoiceRepository.findByStatus(status, pageable);
    }

    public Invoice findById(Long id) {
        return invoiceRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "发票不存在"));
    }

    public List<Invoice> findByUserId(Long userId) {
        return invoiceRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Transactional
    public Invoice create(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    // 开票
    @Transactional
    public Invoice issue(Long id, String invoiceNo, String invoiceUrl) {
        Invoice invoice = findById(id);
        if (!"pending".equals(invoice.getStatus())) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "发票状态不正确");
        }
        invoice.setInvoiceNo(invoiceNo);
        invoice.setInvoiceUrl(invoiceUrl);
        invoice.setStatus("issued");
        invoice.setIssueTime(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    // 开票失败
    @Transactional
    public Invoice fail(Long id, String reason) {
        Invoice invoice = findById(id);
        invoice.setStatus("failed");
        invoice.setFailReason(reason);
        return invoiceRepository.save(invoice);
    }

    public long countByStatus(String status) {
        return invoiceRepository.findByStatus(status, Pageable.unpaged()).getTotalElements();
    }
}
