package tech.wetech.admin3.miniapp.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.wetech.admin3.miniapp.ApiResult;
import tech.wetech.admin3.miniapp.service.ICommonService;
import tech.wetech.admin3.miniapp.service.IUserService;
import tech.wetech.admin3.miniapp.vo.*;
import tech.wetech.admin3.sys.model.MiniappUser;
import tech.wetech.admin3.sys.service.SystemConfigService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序公共模块接口
 */
@RestController("miniappCommonController")
@RequestMapping("/minapp/common")
public class CommonController {

    private final ICommonService commonService;
    private final IUserService userService;
    private final SystemConfigService systemConfigService;

    public CommonController(ICommonService commonService, IUserService userService, SystemConfigService systemConfigService) {
        this.commonService = commonService;
        this.userService = userService;
        this.systemConfigService = systemConfigService;
    }

    /**
     * 上传图片
     */
    @PostMapping("/upload")
    public ApiResult<UploadVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", required = false) String type) {
        UploadVO result = commonService.upload(file, type);
        return ApiResult.ok("上传成功", result);
    }

    /**
     * 获取系统配置
     */
    @GetMapping("/config")
    public ApiResult<SystemConfigVO> getConfig() {
        return ApiResult.ok(commonService.getConfig());
    }

    /**
     * 获取单个配置项
     */
    @GetMapping("/config/{key}")
    public ApiResult<String> getConfigByKey(@PathVariable String key) {
        String value = systemConfigService.getValue(key, "");
        return ApiResult.ok(value);
    }


}
