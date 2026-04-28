package tech.wetech.admin3.sys.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.SystemConfig;
import tech.wetech.admin3.sys.repository.SystemConfigRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置服务
 *
 * @author cjbi
 */
@Service
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    public SystemConfigService(SystemConfigRepository systemConfigRepository) {
        this.systemConfigRepository = systemConfigRepository;
    }

    public List<SystemConfig> findAll() {
        return systemConfigRepository.findAll();
    }

    public SystemConfig findById(Long id) {
        return systemConfigRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST));
    }

    public SystemConfig findByKey(String key) {
        return systemConfigRepository.findByConfigKey(key)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "配置项不存在: " + key));
    }

    public String getValue(String key) {
        return systemConfigRepository.findByConfigKey(key)
            .map(SystemConfig::getConfigValue)
            .orElse(null);
    }

    public String getValue(String key, String defaultValue) {
        return systemConfigRepository.findByConfigKey(key)
            .map(SystemConfig::getConfigValue)
            .orElse(defaultValue);
    }

    public Integer getIntValue(String key, Integer defaultValue) {
        String value = getValue(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public Map<String, String> getConfigMap() {
        return systemConfigRepository.findAll().stream()
            .collect(Collectors.toMap(SystemConfig::getConfigKey, SystemConfig::getConfigValue));
    }

    public List<SystemConfig> findByKeyPrefix(String prefix) {
        return systemConfigRepository.findByKeyPrefix(prefix);
    }

    @Transactional
    public SystemConfig create(String configKey, String configValue, String configType, String description) {
        if (systemConfigRepository.existsByConfigKey(configKey)) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "配置键已存在: " + configKey);
        }
        SystemConfig config = new SystemConfig();
        config.setConfigKey(configKey);
        config.setConfigValue(configValue);
        config.setConfigType(configType != null ? configType : "string");
        config.setDescription(description);
        return systemConfigRepository.save(config);
    }

    @Transactional
    public SystemConfig update(Long id, String configValue, String configType, String description) {
        SystemConfig config = findById(id);
        config.setConfigValue(configValue);
        if (configType != null) {
            config.setConfigType(configType);
        }
        if (description != null) {
            config.setDescription(description);
        }
        return systemConfigRepository.save(config);
    }

    @Transactional
    public void updateByKey(String configKey, String configValue) {
        SystemConfig config = systemConfigRepository.findByConfigKey(configKey)
            .orElseGet(() -> {
                // 配置不存在时自动创建
                SystemConfig newConfig = new SystemConfig();
                newConfig.setConfigKey(configKey);
                newConfig.setConfigType("string");
                return newConfig;
            });
        config.setConfigValue(configValue);
        systemConfigRepository.save(config);
    }

    @Transactional
    public void delete(Long id) {
        SystemConfig config = findById(id);
        systemConfigRepository.delete(config);
    }

    @Transactional
    public void batchUpdate(List<SystemConfig> configs) {
        for (SystemConfig config : configs) {
            if (config.getId() != null) {
                SystemConfig existing = findById(config.getId());
                existing.setConfigValue(config.getConfigValue());
                if (config.getDescription() != null) {
                    existing.setDescription(config.getDescription());
                }
                systemConfigRepository.save(existing);
            }
        }
    }
}
