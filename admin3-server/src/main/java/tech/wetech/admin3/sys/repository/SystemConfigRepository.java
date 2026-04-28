package tech.wetech.admin3.sys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.wetech.admin3.sys.model.SystemConfig;

import java.util.List;
import java.util.Optional;

/**
 * @author cjbi
 */
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    Optional<SystemConfig> findByConfigKey(String configKey);

    boolean existsByConfigKey(String configKey);

    @Query("SELECT c FROM SystemConfig c WHERE c.configKey LIKE :prefix%")
    List<SystemConfig> findByKeyPrefix(String prefix);

    void deleteByConfigKey(String configKey);
}
