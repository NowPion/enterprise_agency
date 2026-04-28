package tech.wetech.admin3.sys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.ProductField;

import java.util.List;

@Repository
public interface ProductFieldRepository extends JpaRepository<ProductField, Long> {
    List<ProductField> findByProductIdAndStatusOrderBySortAsc(Long productId, Integer status);
    List<ProductField> findByProductIdOrderBySortAsc(Long productId);
}
