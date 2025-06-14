package com.codewiki.repositories;

import com.codewiki.models.Article;
import com.codewiki.models.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Найти статью по slug (уникальное поле)
    Optional<Article> findBySlug(String slug);

    // Найти все статьи с определённым статусом (с пагинацией)
    Page<Article> findByStatus(ArticleStatus status, Pageable pageable);

    // Найти все статьи по автору
    List<Article> findByAuthorId(Long authorId);

    // Поиск по заголовку или содержанию (без учёта регистра)
    @Query("SELECT a FROM Article a WHERE LOWER(a.title) LIKE LOWER(concat('%', :query, '%')) OR LOWER(a.content) LIKE LOWER(concat('%', :query, '%'))")
    List<Article> searchArticles(String query);

    // Найти статьи по тегу
    @Query("SELECT a FROM Article a JOIN a.tags t WHERE t.name = :tagName")
    List<Article> findByTagName(String tagName);
}