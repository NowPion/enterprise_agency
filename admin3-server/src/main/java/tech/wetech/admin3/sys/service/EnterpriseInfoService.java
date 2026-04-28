package tech.wetech.admin3.sys.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.sys.model.EnterpriseInfo;
import tech.wetech.admin3.sys.repository.EnterpriseInfoRepository;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnterpriseInfoService {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseInfoService.class);

    private final EnterpriseInfoRepository enterpriseInfoRepository;
    private final SystemConfigService systemConfigService;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    private static final String API_HOST = "https://juccvvb.market.alicloudapi.com";
    private static final String API_PATH = "/enterprise/business/query";
    private static final String CONFIG_KEY_APPCODE = "alibaba.miniapp.reverso";

    public EnterpriseInfoService(EnterpriseInfoRepository enterpriseInfoRepository,
                                  SystemConfigService systemConfigService,
                                  ObjectMapper objectMapper) {
        this.enterpriseInfoRepository = enterpriseInfoRepository;
        this.systemConfigService = systemConfigService;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * 搜索企业信息
     * 1. 先从本地数据库查询
     * 2. 如果本地结果少于5条，调用阿里云API补充
     */
    @Transactional
    public List<EnterpriseInfo> searchEnterprise(String keyword) {
        if (keyword == null || keyword.trim().length() < 2) {
            return new ArrayList<>();
        }
        keyword = keyword.trim();

        // 1. 先从本地数据库查询
        List<EnterpriseInfo> localResults = enterpriseInfoRepository.findByKeyword(keyword);
        
        // 如果本地结果足够多，直接返回
        if (localResults.size() >= 10) {
            return localResults.subList(0, 10);
        }

        // 2. 调用阿里云API获取更多结果
        try {
            List<EnterpriseInfo> apiResults = queryFromApi(keyword);
            
            // 保存API返回的新数据到数据库
            for (EnterpriseInfo info : apiResults) {
                saveOrUpdate(info);
            }
            
            // 重新从数据库查询（包含新保存的数据）
            localResults = enterpriseInfoRepository.findByKeyword(keyword);
        } catch (Exception e) {
            log.error("调用企业信息API失败: {}", e.getMessage());
            // API调用失败，返回本地结果
        }

        return localResults.size() > 10 ? localResults.subList(0, 10) : localResults;
    }

    /**
     * 调用阿里云企业信息查询API
     */
    private List<EnterpriseInfo> queryFromApi(String keyword) throws Exception {
        List<EnterpriseInfo> results = new ArrayList<>();

        // 从系统配置表获取AppCode
        String appCode = systemConfigService.getValue(CONFIG_KEY_APPCODE, "");
        if (appCode == null || appCode.isEmpty()) {
            log.warn("未配置阿里云企业查询AppCode，配置键: {}", CONFIG_KEY_APPCODE);
            return results;
        }

        log.info("调用阿里云企业信息查询API, keyword: {}", keyword);

        // 构建请求体
        String body = "keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) +
                "&pageNo=1&pageSize=10";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_HOST + API_PATH))
                .header("Authorization", "APPCODE " + appCode)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        log.info("阿里云API响应状态: {}", response.statusCode());
        
        if (response.statusCode() == 200) {
            JsonNode root = objectMapper.readTree(response.body());
            int code = root.path("code").asInt();
            
            if (code == 200) {
                JsonNode resultArray = root.path("data").path("result");
                if (resultArray.isArray()) {
                    for (JsonNode item : resultArray) {
                        EnterpriseInfo info = new EnterpriseInfo();
                        info.setCompanyName(item.path("companyName").asText(""));
                        info.setCreditNo(item.path("creditNo").asText(""));
                        info.setCompanyCode(item.path("companyCode").asText(""));
                        info.setLegalPerson(item.path("legalPerson").asText(""));
                        info.setCompanyStatus(item.path("companyStatus").asText(""));
                        info.setEstablishDate(item.path("establishDate").asText(""));
                        
                        if (!info.getCompanyName().isEmpty()) {
                            results.add(info);
                        }
                    }
                    log.info("阿里云API返回 {} 条企业信息", results.size());
                }
            } else {
                log.warn("企业信息API返回错误: code={}, msg={}", code, root.path("msg").asText());
            }
        } else {
            log.error("企业信息API请求失败: status={}, body={}", response.statusCode(), response.body());
        }

        return results;
    }

    /**
     * 保存或更新企业信息
     */
    private void saveOrUpdate(EnterpriseInfo info) {
        if (info.getCreditNo() == null || info.getCreditNo().isEmpty()) {
            // 没有信用代码，按公司名称查找
            enterpriseInfoRepository.findByCompanyName(info.getCompanyName())
                    .ifPresentOrElse(
                            existing -> {
                                // 更新现有记录
                                existing.setLegalPerson(info.getLegalPerson());
                                existing.setCompanyStatus(info.getCompanyStatus());
                                existing.setEstablishDate(info.getEstablishDate());
                                enterpriseInfoRepository.save(existing);
                            },
                            () -> enterpriseInfoRepository.save(info)
                    );
        } else {
            // 有信用代码，按信用代码查找
            enterpriseInfoRepository.findByCreditNo(info.getCreditNo())
                    .ifPresentOrElse(
                            existing -> {
                                // 更新现有记录
                                existing.setCompanyName(info.getCompanyName());
                                existing.setLegalPerson(info.getLegalPerson());
                                existing.setCompanyStatus(info.getCompanyStatus());
                                existing.setEstablishDate(info.getEstablishDate());
                                enterpriseInfoRepository.save(existing);
                            },
                            () -> enterpriseInfoRepository.save(info)
                    );
        }
    }
}
