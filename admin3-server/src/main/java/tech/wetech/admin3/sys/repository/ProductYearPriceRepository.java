package tech.wetech.admin3.sys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.wetech.admin3.sys.model.ProductYearPrice;

import java.util.List;

public interface ProductYearPriceRepository extends JpaRepository<ProductYearPrice, Long> {
    
    List<ProductYearPrice> findByProductIdAndStatusOrderBySortAsc(Long productId, Integer status);
    
    List<ProductYearPrice> findByProductIdOrderBySortAsc(Long productId);
    
    void deleteByProductId(Long productId);
}
