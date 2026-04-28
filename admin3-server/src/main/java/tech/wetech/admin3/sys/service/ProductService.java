package tech.wetech.admin3.sys.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.Product;
import tech.wetech.admin3.sys.model.ProductField;
import tech.wetech.admin3.sys.model.ProductYearPrice;
import tech.wetech.admin3.sys.repository.ProductFieldRepository;
import tech.wetech.admin3.sys.repository.ProductRepository;
import tech.wetech.admin3.sys.repository.ProductYearPriceRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductFieldRepository productFieldRepository;
    private final ProductYearPriceRepository productYearPriceRepository;

    public ProductService(ProductRepository productRepository, 
                         ProductFieldRepository productFieldRepository,
                         ProductYearPriceRepository productYearPriceRepository) {
        this.productRepository = productRepository;
        this.productFieldRepository = productFieldRepository;
        this.productYearPriceRepository = productYearPriceRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findEnabled() {
        return productRepository.findByStatusOrderBySortDesc(1);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategoryAndStatusOrderBySortDesc(category, 1);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "产品不存在"));
    }

    @Transactional
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, Product product) {
        Product existing = findById(id);
        if (product.getName() != null) existing.setName(product.getName());
        if (product.getCode() != null) existing.setCode(product.getCode());
        if (product.getCategory() != null) existing.setCategory(product.getCategory());
        if (product.getImage() != null) existing.setImage(product.getImage());
        if (product.getDescription() != null) existing.setDescription(product.getDescription());
        if (product.getOriginalPrice() != null) existing.setOriginalPrice(product.getOriginalPrice());
        if (product.getPrice() != null) existing.setPrice(product.getPrice());
        if (product.getProcessDays() != null) existing.setProcessDays(product.getProcessDays());
        if (product.getNotice() != null) existing.setNotice(product.getNotice());
        if (product.getMaterials() != null) existing.setMaterials(product.getMaterials());
        if (product.getSort() != null) existing.setSort(product.getSort());
        if (product.getStatus() != null) existing.setStatus(product.getStatus());
        return productRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    // 产品字段管理
    public List<ProductField> findFieldsByProductId(Long productId) {
        return productFieldRepository.findByProductIdOrderBySortAsc(productId);
    }

    @Transactional
    public ProductField createField(ProductField field) {
        return productFieldRepository.save(field);
    }

    @Transactional
    public ProductField updateField(Long id, ProductField field) {
        ProductField existing = productFieldRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "字段不存在"));
        if (field.getFieldName() != null) existing.setFieldName(field.getFieldName());
        if (field.getFieldKey() != null) existing.setFieldKey(field.getFieldKey());
        if (field.getFieldType() != null) existing.setFieldType(field.getFieldType());
        if (field.getPlaceholder() != null) existing.setPlaceholder(field.getPlaceholder());
        if (field.getOptions() != null) existing.setOptions(field.getOptions());
        if (field.getIsRequired() != null) existing.setIsRequired(field.getIsRequired());
        if (field.getValidation() != null) existing.setValidation(field.getValidation());
        if (field.getErrorMsg() != null) existing.setErrorMsg(field.getErrorMsg());
        if (field.getSort() != null) existing.setSort(field.getSort());
        if (field.getStatus() != null) existing.setStatus(field.getStatus());
        return productFieldRepository.save(existing);
    }

    @Transactional
    public void deleteField(Long id) {
        productFieldRepository.deleteById(id);
    }

    // 产品年度价格管理
    public List<ProductYearPrice> findYearPricesByProductId(Long productId) {
        return productYearPriceRepository.findByProductIdOrderBySortAsc(productId);
    }

    @Transactional
    public ProductYearPrice createYearPrice(Long productId, String year, BigDecimal originalPrice, BigDecimal price, Integer sort) {
        ProductYearPrice yearPrice = new ProductYearPrice();
        yearPrice.setProductId(productId);
        yearPrice.setYear(year);
        yearPrice.setOriginalPrice(originalPrice);
        yearPrice.setPrice(price);
        yearPrice.setSort(sort != null ? sort : 0);
        yearPrice.setStatus(1);
        return productYearPriceRepository.save(yearPrice);
    }

    @Transactional
    public ProductYearPrice updateYearPrice(Long id, String year, BigDecimal originalPrice, BigDecimal price, Integer sort, Integer status) {
        ProductYearPrice existing = productYearPriceRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "年度价格不存在"));
        if (year != null) existing.setYear(year);
        if (originalPrice != null) existing.setOriginalPrice(originalPrice);
        if (price != null) existing.setPrice(price);
        if (sort != null) existing.setSort(sort);
        if (status != null) existing.setStatus(status);
        return productYearPriceRepository.save(existing);
    }

    @Transactional
    public void deleteYearPrice(Long id) {
        productYearPriceRepository.deleteById(id);
    }
}
