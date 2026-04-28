package tech.wetech.admin3.miniapp.service;

import org.springframework.web.multipart.MultipartFile;
import tech.wetech.admin3.miniapp.vo.*;

import java.util.List;

/**
 * 公共模块服务接口
 */
public interface ICommonService {

    /**
     * 上传图片
     */
    UploadVO upload(MultipartFile file, String type);

    /**
     * 获取系统配置
     */
    SystemConfigVO getConfig();


    /**
     * 检查版本更新
     */
    VersionVO checkUpdate(String version, String platform);


}
