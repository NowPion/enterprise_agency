package tech.wetech.admin3.miniapp.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.miniapp.service.IMiniappProductService;
import tech.wetech.admin3.miniapp.vo.PageVO;
import tech.wetech.admin3.miniapp.vo.ProductVO;
import tech.wetech.admin3.sys.model.Product;
import tech.wetech.admin3.sys.model.ProductField;
import tech.wetech.admin3.sys.model.ProductYearPrice;
import tech.wetech.admin3.sys.repository.ProductFieldRepository;
import tech.wetech.admin3.sys.repository.ProductRepository;
import tech.wetech.admin3.sys.repository.ProductYearPriceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MiniappProductServiceImpl implements IMiniappProductService {

    private final ProductRepository productRepository;
    private final ProductFieldRepository productFieldRepository;
    private final ProductYearPriceRepository productYearPriceRepository;

    public MiniappProductServiceImpl(ProductRepository productRepository, 
                                     ProductFieldRepository productFieldRepository,
                                     ProductYearPriceRepository productYearPriceRepository) {
        this.productRepository = productRepository;
        this.productFieldRepository = productFieldRepository;
        this.productYearPriceRepository = productYearPriceRepository;
    }

    @Override
    public PageVO<ProductVO> getProductList(String category, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "sort"));
        Page<Product> productPage;
        if (category != null && !category.isEmpty()) {
            productPage = productRepository.findByCategoryAndStatus(category, 1, pageRequest);
        } else {
            productPage = productRepository.findByStatus(1, pageRequest);
        }
        
        List<ProductVO> list = productPage.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return new PageVO<>(list, productPage.getTotalElements());
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "产品不存在"));
        
        if (product.getStatus() != 1) {
            throw new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "产品已下架");
        }
        
        ProductVO vo = convertToVO(product);
        // 获取产品表单字段
        List<ProductField> fields = productFieldRepository.findByProductIdAndStatusOrderBySortAsc(id, 1);
        vo.setFields(fields.stream().map(this::convertFieldToVO).collect(Collectors.toList()));
        // 获取年度价格
        List<ProductYearPrice> yearPrices = productYearPriceRepository.findByProductIdAndStatusOrderBySortAsc(id, 1);
        vo.setYearPrices(yearPrices.stream().map(this::convertYearPriceToVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public List<String> getCategories() {
        return productRepository.findDistinctCategories();
    }

    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setCode(product.getCode());
        vo.setCategory(product.getCategory());
        vo.setDescription(product.getDescription());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setPrice(product.getPrice());
        vo.setProcessDays(product.getProcessDays());
        vo.setNotice(product.getNotice());
        vo.setMaterials(product.getMaterials());
        return vo;
    }

    private ProductVO.ProductFieldVO convertFieldToVO(ProductField field) {
        ProductVO.ProductFieldVO vo = new ProductVO.ProductFieldVO();
        vo.setId(field.getId());
        vo.setFieldName(field.getFieldName());
        vo.setFieldKey(field.getFieldKey());
        vo.setFieldType(field.getFieldType());
        vo.setPlaceholder(field.getPlaceholder());
        vo.setIsRequired(field.getIsRequired() != null && field.getIsRequired() == 1);
        vo.setSort(field.getSort());
        return vo;
    }

    private ProductVO.YearPriceVO convertYearPriceToVO(ProductYearPrice yearPrice) {
        ProductVO.YearPriceVO vo = new ProductVO.YearPriceVO();
        vo.setId(yearPrice.getId());
        vo.setYear(yearPrice.getYear());
        vo.setOriginalPrice(yearPrice.getOriginalPrice());
        vo.setPrice(yearPrice.getPrice());
        return vo;
    }
}
