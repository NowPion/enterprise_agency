package tech.wetech.admin3.miniapp.service;

import tech.wetech.admin3.miniapp.vo.PageVO;
import tech.wetech.admin3.miniapp.vo.ProductVO;

import java.util.List;

/**
 * 小程序产品服务接口
 */
public interface IMiniappProductService {
    
    /**
     * 获取产品列表
     */
    PageVO<ProductVO> getProductList(String category, int page, int size);
    
    /**
     * 获取产品详情
     */
    ProductVO getProductDetail(Long id);
    
    /**
     * 获取产品分类列表
     */
    List<String> getCategories();
}
