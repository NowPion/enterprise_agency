package tech.wetech.admin3.miniapp.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.miniapp.service.ICommonService;
import tech.wetech.admin3.miniapp.vo.*;

import tech.wetech.admin3.sys.service.StorageService;
import tech.wetech.admin3.sys.service.SystemConfigService;
import tech.wetech.admin3.sys.service.dto.StorageFileDTO;

import java.io.IOException;
import java.util.List;

/**
 * 公共模块服务实现
 */
@Service
public class CommonServiceImpl implements ICommonService {

    private final StorageService storageService;
    private final SystemConfigService configService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/jpg", "image/png", "image/gif");

    public CommonServiceImpl(StorageService storageService,
                             SystemConfigService configService) {
        this.storageService = storageService;
        this.configService = configService;
    }

    @Override
    public UploadVO upload(MultipartFile file, String type) {
        validateFile(file);
        try {
            StorageFileDTO storageFile = storageService.store(
                null,
                file.getInputStream(),
                file.getSize(),
                file.getContentType(),
                file.getOriginalFilename()
            );
            return new UploadVO(storageFile.getUrl(), storageFile.getKey(), storageFile.getSize());
        } catch (IOException e) {
            throw new BusinessException(CommonResultStatus.FAIL, "上传失败");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "请选择文件");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "文件格式不支持");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(CommonResultStatus.PARAM_ERROR, "文件大小超限");
        }
    }

    @Override
    public SystemConfigVO getConfig() {
        SystemConfigVO vo = new SystemConfigVO();
        vo.setAppName(configService.getValue("app_name", "舌苔舌诊"));
        vo.setVersion(configService.getValue("app_version", "1.0.0"));
        vo.setServicePhone(configService.getValue("service_phone", ""));
        vo.setServiceWechat(configService.getValue("service_wechat", ""));
        vo.setServiceWorkTime(configService.getValue("service_work_time", ""));
        vo.setRegisterReward(configService.getIntValue("register_reward_count", 3));
        vo.setInviteReward(configService.getIntValue("invite_reward_count", 5));
        vo.setAdReward(configService.getIntValue("ad_reward_count", 1));
        return vo;
    }





    @Override
    public VersionVO checkUpdate(String version, String platform) {
        String latestVersion = configService.getValue("app_version", "1.0.0");
        String updateContent = configService.getValue("app_update_content", "");
        boolean forceUpdate = "true".equals(configService.getValue("app_force_update", "false"));

        if (compareVersion(version, latestVersion) >= 0) {
            return VersionVO.noUpdate(version);
        }

        VersionVO vo = new VersionVO();
        vo.setHasUpdate(true);
        vo.setVersion(latestVersion);
        vo.setContent(updateContent);
        vo.setForceUpdate(forceUpdate);
        vo.setDownloadUrl("");
        return vo;
    }

    private int compareVersion(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (num1 != num2) return num1 - num2;
        }
        return 0;
    }

}
