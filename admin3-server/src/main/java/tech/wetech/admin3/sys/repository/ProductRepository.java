package tech.wetech.admin3.sys.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatusOrderBySortDesc(Integer status);
    List<Product> findByCategoryAndStatusOrderBySortDesc(String category, Integer status);
    Optional<Product> findByCode(String code);
    
    // 小程序端分页查询
    Page<Product> findByStatus(Integer status, Pageable pageable);
    Page<Product> findByCategoryAndStatus(String category, Integer status, Pageable pageable);
    
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.status = 1")
    List<String> findDistinctCategories();
}
