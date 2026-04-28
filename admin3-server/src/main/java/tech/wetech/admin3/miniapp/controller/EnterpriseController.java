package tech.wetech.admin3.miniapp.controller;

import org.springframework.web.bind.annotation.*;
import tech.wetech.admin3.miniapp.ApiResult;
import tech.wetech.admin3.sys.model.EnterpriseInfo;
import tech.wetech.admin3.sys.service.EnterpriseInfoService;

import java.util.List;

/**
 * 小程序端 - 企业信息查询
 */
@RestController
@RequestMapping("/minapp/enterprise")
public class EnterpriseController {

    private final EnterpriseInfoService enterpriseInfoService;

    public EnterpriseController(EnterpriseInfoService enterpriseInfoService) {
        this.enterpriseInfoService = enterpriseInfoService;
    }

    /**
     * 搜索企业信息
     */
    @GetMapping("/search")
    public ApiResult<List<EnterpriseInfo>> search(@RequestParam String keyword) {
        List<EnterpriseInfo> list = enterpriseInfoService.searchEnterprise(keyword);
        return ApiResult.ok(list);
    }
}
