package tech.wetech.admin3.miniapp.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.miniapp.service.IMiniappContentService;
import tech.wetech.admin3.miniapp.vo.ContentVO;
import tech.wetech.admin3.miniapp.vo.PageVO;
import tech.wetech.admin3.sys.model.Article;
import tech.wetech.admin3.sys.model.Faq;
import tech.wetech.admin3.sys.model.Notice;
import tech.wetech.admin3.sys.repository.ArticleRepository;
import tech.wetech.admin3.sys.repository.FaqRepository;
import tech.wetech.admin3.sys.repository.NoticeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MiniappContentServiceImpl implements IMiniappContentService {

    private final NoticeRepository noticeRepository;
    private final FaqRepository faqRepository;
    private final ArticleRepository articleRepository;

    public MiniappContentServiceImpl(NoticeRepository noticeRepository, FaqRepository faqRepository, 
                                     ArticleRepository articleRepository) {
        this.noticeRepository = noticeRepository;
        this.faqRepository = faqRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public PageVO<ContentVO> getNoticeList(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Notice> noticePage = noticeRepository.findByStatus(1, pageRequest);
        
        List<ContentVO> list = noticePage.getContent().stream()
                .map(this::convertNoticeToVO)
                .collect(Collectors.toList());
        
        return new PageVO<>(list, noticePage.getTotalElements());
    }

    @Override
    public ContentVO getNoticeDetail(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "公告不存在"));
        return convertNoticeToVO(notice);
    }

    @Override
    public PageVO<ContentVO> getFaqList(String category, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "sort"));
        Page<Faq> faqPage;
        if (category != null && !category.isEmpty()) {
            faqPage = faqRepository.findByCategoryAndStatus(category, 1, pageRequest);
        } else {
            faqPage = faqRepository.findByStatus(1, pageRequest);
        }
        
        List<ContentVO> list = faqPage.getContent().stream()
                .map(this::convertFaqToVO)
                .collect(Collectors.toList());
        
        return new PageVO<>(list, faqPage.getTotalElements());
    }

    @Override
    public PageVO<ContentVO> getArticleList(String category, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Article> articlePage;
        if (category != null && !category.isEmpty()) {
            articlePage = articleRepository.findByCategoryAndStatus(category, 1, pageRequest);
        } else {
            articlePage = articleRepository.findByStatus(1, pageRequest);
        }
        
        List<ContentVO> list = articlePage.getContent().stream()
                .map(this::convertArticleToVO)
                .collect(Collectors.toList());
        
        return new PageVO<>(list, articlePage.getTotalElements());
    }

    @Override
    public ContentVO getArticleDetail(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "文章不存在"));
        
        // 增加阅读量
        article.setViewCount(article.getViewCount() + 1);
        articleRepository.save(article);
        
        return convertArticleToVO(article);
    }

    private ContentVO convertNoticeToVO(Notice notice) {
        ContentVO vo = new ContentVO();
        vo.setId(notice.getId());
        vo.setTitle(notice.getTitle());
        vo.setContent(notice.getContent());
        vo.setCreateTime(notice.getCreateTime());
        return vo;
    }

    private ContentVO convertFaqToVO(Faq faq) {
        ContentVO vo = new ContentVO();
        vo.setId(faq.getId());
        vo.setTitle(faq.getQuestion());
        vo.setContent(faq.getAnswer());
        vo.setCategory(faq.getCategory());
        return vo;
    }

    private ContentVO convertArticleToVO(Article article) {
        ContentVO vo = new ContentVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setContent(article.getContent());
        vo.setCategory(article.getCategory());
        vo.setCoverImage(article.getCover());
        vo.setViewCount(article.getViewCount());
        vo.setCreateTime(article.getCreateTime());
        return vo;
    }
}
