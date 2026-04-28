package tech.wetech.admin3.sys.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.Article;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByStatusOrderBySortDescCreateTimeDesc(Integer status);
    List<Article> findByArticleTypeAndStatusOrderBySortDesc(String articleType, Integer status);
    List<Article> findByCategoryAndStatusOrderBySortDesc(String category, Integer status);
    Page<Article> findByStatus(Integer status, Pageable pageable);
    Page<Article> findByCategoryAndStatus(String category, Integer status, Pageable pageable);
}
