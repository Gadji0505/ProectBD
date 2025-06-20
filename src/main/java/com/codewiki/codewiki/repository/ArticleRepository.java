package com.codewiki.codewiki.repository;

import com.codewiki.codewiki.model.Article;
import com.codewiki.codewiki.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {
    
    // Основные методы поиска
    Optional<Article> findBySlug(String slug);
    List<Article> findByAuthor(User author);
    List<Article> findTop5ByOrderByCreatedAtDesc();
    List<Article> findByIsPublishedTrueOrderByCreatedAtDesc();
    
    // Полностью переработанный поиск по заголовку или содержанию
    @Query("SELECT a FROM Article a WHERE " +
           "a.title LIKE %:searchTerm% OR " +
           "a.content LIKE %:searchTerm%")
    List<Article> searchArticles(@Param("searchTerm") String searchTerm);
    
    // Алиас для searchArticles
    default List<Article> searchByTitleOrContent(String query) {
        return searchArticles("%" + query.toLowerCase() + "%");
    }
    
    // Поиск по категории
    List<Article> findByCategoryId(Long categoryId);
    Page<Article> findByCategoryId(Long categoryId, Pageable pageable);
    
    // Методы для работы с тегами
    @Query("SELECT a FROM Article a JOIN a.tags t WHERE t.id = :tagId")
    List<Article> findByTagId(@Param("tagId") Long tagId);
    
    @Query("SELECT DISTINCT a FROM Article a JOIN a.tags t WHERE t.id IN :tagIds")
    List<Article> findByTagsIds(@Param("tagIds") Set<Long> tagIds);
    
    // Методы подсчета
    long countByAuthor(User author);
    long countByCategoryId(Long categoryId);
    long countByTagsId(Long tagId);
    long countByIsPublishedTrue();
    
    // Проверки существования
    boolean existsBySlug(String slug);
    boolean existsByTitle(String title);
    
    // Методы для пагинации
    Page<Article> findAll(Pageable pageable);
    
    @Query("SELECT a FROM Article a ORDER BY a.createdAt DESC")
    Page<Article> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<Article> findByAuthor(User author, Pageable pageable);
    Page<Article> findByIsPublishedTrue(Pageable pageable);
    
    // Методы для популярного контента
    @Query("SELECT a FROM Article a ORDER BY a.createdAt DESC")
    Page<Article> findPopularArticles(Pageable pageable);
    
    default List<Article> findPopularArticles(int limit) {
        return findPopularArticles(Pageable.ofSize(limit)).getContent();
    }
    
    // Исправленный расширенный поиск
    @Query("SELECT DISTINCT a FROM Article a " +
           "LEFT JOIN a.tags t " +
           "WHERE (:searchTerm IS NULL OR " +
           "      (a.title LIKE %:searchTerm% OR " +
           "       a.content LIKE %:searchTerm%)) " +
           "AND (:categoryId IS NULL OR a.category.id = :categoryId) " +
           "AND (:tagIds IS NULL OR t.id IN :tagIds)")
    Page<Article> complexSearch(
            @Param("searchTerm") String searchTerm,
            @Param("categoryId") Long categoryId,
            @Param("tagIds") Set<Long> tagIds,
            Pageable pageable);
    
    // Методы для статистики
    @Query("SELECT COUNT(a) FROM Article a")
    long getTotalArticlesCount();
    
    @Query("SELECT COUNT(a) FROM Article a WHERE a.isPublished = true")
    long getPublishedArticlesCount();
    
    // Методы для авторов
    @Query("SELECT a FROM Article a WHERE a.author.id = :authorId")
    List<Article> findByAuthorId(@Param("authorId") Long authorId);
    
    @Query("SELECT a FROM Article a WHERE a.author.username = :username")
    List<Article> findByAuthorUsername(@Param("username") String username);
    
    // Методы для получения последних статей
    @Query("SELECT a FROM Article a ORDER BY a.createdAt DESC")
    List<Article> findLatestArticles(Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.isPublished = true ORDER BY a.createdAt DESC")
    List<Article> findLatestPublishedArticles(Pageable pageable);
    
    // Методы для избранного контента
    @Query("SELECT a FROM Article a WHERE a.isFeatured = true ORDER BY a.createdAt DESC")
    List<Article> findFeaturedArticles(Pageable pageable);
    
    // Методы для поиска по статусу публикации
    List<Article> findByIsPublished(boolean isPublished);
    Page<Article> findByIsPublished(boolean isPublished, Pageable pageable);
    
    // Методы для поиска по нескольким критериям
    @Query("SELECT a FROM Article a WHERE " +
           "(:title IS NULL OR a.title LIKE %:title%) AND " +
           "(:content IS NULL OR a.content LIKE %:content%) AND " +
           "(:isPublished IS NULL OR a.isPublished = :isPublished)")
    Page<Article> findByMultipleCriteria(
            @Param("title") String title,
            @Param("content") String content,
            @Param("isPublished") Boolean isPublished,
            Pageable pageable);
}