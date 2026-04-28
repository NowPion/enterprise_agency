package tech.wetech.admin3.miniapp.service;

import tech.wetech.admin3.miniapp.dto.ApplyInvoiceDTO;
import tech.wetech.admin3.miniapp.vo.InvoiceVO;
import tech.wetech.admin3.miniapp.vo.PageVO;

/**
 * 小程序发票服务接口
 */
public interface IMiniappInvoiceService {
    
    /**
     * 申请发票
     */
    InvoiceVO applyInvoice(Long userId, ApplyInvoiceDTO dto);
    
    /**
     * 获取发票列表
     */
    PageVO<InvoiceVO> getInvoiceList(Long userId, int page, int size);
    
    /**
     * 获取发票详情
     */
    InvoiceVO getInvoiceDetail(Long userId, Long invoiceId);
    
    /**
     * 检查订单是否可开票
     */
    boolean canApplyInvoice(Long userId, Long orderId);
}
