package tech.wetech.admin3.miniapp.service;

import tech.wetech.admin3.miniapp.vo.BannerVO;
import tech.wetech.admin3.miniapp.vo.HomeStatsVO;
import tech.wetech.admin3.miniapp.vo.PageVO;

/**
 * 小程序首页服务接口
 */
public interface IHomeService {

    /**
     * 获取轮播图列表
     */
    PageVO<BannerVO> getBanners();



    /**
     * 获取首页统计数据
     */
    HomeStatsVO getStats();
}
