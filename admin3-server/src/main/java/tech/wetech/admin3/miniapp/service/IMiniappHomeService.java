package tech.wetech.admin3.miniapp.service;

import tech.wetech.admin3.miniapp.vo.BannerVO;
import tech.wetech.admin3.miniapp.vo.HomeDataVO;

import java.util.List;

/**
 * 小程序首页服务接口
 */
public interface IMiniappHomeService {
    
    /**
     * 获取首页数据
     */
    HomeDataVO getHomeData();
    
    /**
     * 获取轮播图列表
     */
    List<BannerVO> getBanners();
    
    /**
     * 获取示例进度
     */
    List<HomeDataVO.ExampleProgressVO> getExampleProgress();
}
