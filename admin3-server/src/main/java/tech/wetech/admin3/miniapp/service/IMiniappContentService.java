package tech.wetech.admin3.miniapp.service;

import tech.wetech.admin3.miniapp.vo.ContentVO;
import tech.wetech.admin3.miniapp.vo.PageVO;

/**
 * 小程序内容服务接口
 */
public interface IMiniappContentService {
    
    /**
     * 获取公告列表
     */
    PageVO<ContentVO> getNoticeList(int page, int size);
    
    /**
     * 获取公告详情
     */
    ContentVO getNoticeDetail(Long id);
    
    /**
     * 获取FAQ列表
     */
    PageVO<ContentVO> getFaqList(String category, int page, int size);
    
    /**
     * 获取文章列表
     */
    PageVO<ContentVO> getArticleList(String category, int page, int size);
    
    /**
     * 获取文章详情
     */
    ContentVO getArticleDetail(Long id);
}
