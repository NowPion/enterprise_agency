package tech.wetech.admin3.sys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.OrderMaterial;

import java.util.List;

@Repository
public interface OrderMaterialRepository extends JpaRepository<OrderMaterial, Long> {
    List<OrderMaterial> findByOrderIdOrderByCreateTimeDesc(Long orderId);
    List<OrderMaterial> findByOrderNoOrderByCreateTimeDesc(String orderNo);
}
