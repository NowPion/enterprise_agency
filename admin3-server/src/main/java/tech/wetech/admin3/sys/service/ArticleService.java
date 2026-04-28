package tech.wetech.admin3.sys.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wetech.admin3.common.BusinessException;
import tech.wetech.admin3.common.CommonResultStatus;
import tech.wetech.admin3.sys.model.Article;
import tech.wetech.admin3.sys.repository.ArticleRepository;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public List<Article> findEnabled() {
        return articleRepository.findByStatusOrderBySortDescCreateTimeDesc(1);
    }

    public Page<Article> findEnabled(Pageable pageable) {
        return articleRepository.findByStatus(1, pageable);
    }

    public List<Article> findByType(String articleType) {
        return articleRepository.findByArticleTypeAndStatusOrderBySortDesc(articleType, 1);
    }

    public List<Article> findByCategory(String category) {
        return articleRepository.findByCategoryAndStatusOrderBySortDesc(category, 1);
    }

    public Article findById(Long id) {
        return articleRepository.findById(id)
            .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST, "文章不存在"));
    }

    @Transactional
    public Article create(Article article) {
        return articleRepository.save(article);
    }

    @Transactional
    public Article update(Long id, Article article) {
        Article existing = findById(id);
        if (article.getTitle() != null) existing.setTitle(article.getTitle());
        if (article.getContent() != null) existing.setContent(article.getContent());
        if (article.getCover() != null) existing.setCover(article.getCover());
        if (article.getSummary() != null) existing.setSummary(article.getSummary());
        if (article.getArticleType() != null) existing.setArticleType(article.getArticleType());
        if (article.getCategory() != null) existing.setCategory(article.getCategory());
        if (article.getSort() != null) existing.setSort(article.getSort());
        if (article.getStatus() != null) existing.setStatus(article.getStatus());
        return articleRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }

    @Transactional
    public void incrementViewCount(Long id) {
        Article article = findById(id);
        article.setViewCount(article.getViewCount() + 1);
        articleRepository.save(article);
    }
}
