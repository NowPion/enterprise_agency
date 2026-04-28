package tech.wetech.admin3.sys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.EnterpriseInfo;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnterpriseInfoRepository extends JpaRepository<EnterpriseInfo, Long> {

    /**
     * 根据公司名称模糊查询
     */
    @Query("SELECT e FROM EnterpriseInfo e WHERE e.companyName LIKE %:keyword% ORDER BY e.updateTime DESC")
    List<EnterpriseInfo> findByKeyword(@Param("keyword") String keyword);

    /**
     * 根据统一社会信用代码查询
     */
    Optional<EnterpriseInfo> findByCreditNo(String creditNo);

    /**
     * 根据公司名称精确查询
     */
    Optional<EnterpriseInfo> findByCompanyName(String companyName);
}
