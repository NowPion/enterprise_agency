package tech.wetech.admin3.sys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.ProductCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findByStatusOrderBySortDesc(Integer status);
    List<ProductCategory> findAllByOrderBySortDesc();
    Optional<ProductCategory> findByCode(String code);
    boolean existsByCode(String code);
}
