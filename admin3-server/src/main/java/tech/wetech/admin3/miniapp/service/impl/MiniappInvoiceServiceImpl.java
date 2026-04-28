package tech.wetech.admin3.miniapp.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.miniapp.dto.ApplyInvoiceDTO;
import tech.wetech.admin3.miniapp.service.IMiniappInvoiceService;
import tech.wetech.admin3.miniapp.vo.InvoiceVO;
import tech.wetech.admin3.miniapp.vo.PageVO;
import tech.wetech.admin3.sys.model.Invoice;
import tech.wetech.admin3.sys.model.Order;
import tech.wetech.admin3.sys.repository.InvoiceRepository;
import tech.wetech.admin3.sys.repository.OrderRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class MiniappInvoiceServiceImpl implements IMiniappInvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;

    public MiniappInvoiceServiceImpl(InvoiceRepository invoiceRepository, OrderRepository orderRepository) {
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public InvoiceVO applyInvoice(Long userId, ApplyInvoiceDTO dto) {
        // 检查订单
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无权操作此订单");
        }
        
        // 只有已支付或已完成的订单才能开票
        if (!"paid".equals(order.getStatus()) && !"completed".equals(order.getStatus())) {
            throw new BusinessException(CommonResultStatus.FAIL, "订单状态不支持开票");
        }
        
        // 检查是否已申请过发票
        if (invoiceRepository.existsByOrderId(dto.getOrderId())) {
            throw new BusinessException(CommonResultStatus.FAIL, "该订单已申请过发票");
        }
        
        // 企业发票必须填写税号
        if (dto.getTitleType() == 2 && (dto.getTaxNo() == null || dto.getTaxNo().isEmpty())) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "企业发票必须填写税号");
        }
        
        Invoice invoice = new Invoice();
        invoice.setOrderId(dto.getOrderId());
        invoice.setOrderNo(order.getOrderNo());
        invoice.setUserId(userId);
        invoice.setInvoiceType(dto.getType() == 1 ? "normal" : "special");
        invoice.setTitleType(dto.getTitleType() == 1 ? "personal" : "company");
        invoice.setTitle(dto.getTitle());
        invoice.setTaxNo(dto.getTaxNo());
        invoice.setAmount(order.getPayAmount() != null ? order.getPayAmount() : order.getAmount());
        invoice.setEmail(dto.getEmail());
        invoice.setStatus("pending");
        
        invoice = invoiceRepository.save(invoice);
        return convertToVO(invoice);
    }

    @Override
    public PageVO<InvoiceVO> getInvoiceList(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Invoice> invoicePage = invoiceRepository.findByUserId(userId, pageRequest);
        
        List<InvoiceVO> list = invoicePage.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return new PageVO<>(list, invoicePage.getTotalElements());
    }

    @Override
    public InvoiceVO getInvoiceDetail(Long userId, Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "发票不存在"));
        
        if (!invoice.getUserId().equals(userId)) {
            throw new BusinessException(CommonResultStatus.UNAUTHORIZED, "无权查看此发票");
        }
        
        return convertToVO(invoice);
    }

    @Override
    public boolean canApplyInvoice(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有已支付或已完成的订单才能开票
        if (!"paid".equals(order.getStatus()) && !"completed".equals(order.getStatus())) {
            return false;
        }
        
        // 检查是否已申请过发票
        return !invoiceRepository.existsByOrderId(orderId);
    }

    private InvoiceVO convertToVO(Invoice invoice) {
        InvoiceVO vo = new InvoiceVO();
        vo.setId(invoice.getId());
        vo.setInvoiceNo(invoice.getInvoiceNo());
        vo.setOrderId(invoice.getOrderId());
        vo.setOrderNo(invoice.getOrderNo());
        vo.setType("special".equals(invoice.getInvoiceType()) ? 2 : 1);
        vo.setTypeText("special".equals(invoice.getInvoiceType()) ? "增值税专用发票" : "普通发票");
        vo.setTitleType("company".equals(invoice.getTitleType()) ? 2 : 1);
        vo.setTitle(invoice.getTitle());
        vo.setTaxNo(invoice.getTaxNo());
        vo.setAmount(invoice.getAmount());
        vo.setStatus(invoice.getStatus());
        vo.setStatusText(getStatusText(invoice.getStatus()));
        vo.setInvoiceUrl(invoice.getInvoiceUrl());
        vo.setCreateTime(invoice.getCreateTime());
        vo.setIssueTime(invoice.getIssueTime());
        return vo;
    }

    private String getStatusText(String status) {
        return switch (status) {
            case "pending" -> "待开票";
            case "issued" -> "已开票";
            case "failed" -> "开票失败";
            default -> "未知";
        };
    }
}
