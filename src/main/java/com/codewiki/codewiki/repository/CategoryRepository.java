package com.codewiki.codewiki.repository;

import com.codewiki.codewiki.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Основные методы поиска
    Optional<Category> findByName(String name);
    Optional<Category> findBySlug(String slug);
    Optional<Category> findByNameIgnoreCase(String name);
    Optional<Category> findBySlugIgnoreCase(String slug);
    
    // Методы для списков
    List<Category> findAllByOrderByNameAsc();
    
    // Методы проверки существования
    boolean existsByName(String name);
    boolean existsByNameIgnoreCase(String name);
    boolean existsBySlug(String slug);
    boolean existsBySlugIgnoreCase(String slug);
    
    // Методы для пагинации
    Page<Category> findAll(Pageable pageable);
    
    // Методы для популярных категорий
    @Query("SELECT c FROM Category c LEFT JOIN c.articles a GROUP BY c.id ORDER BY COUNT(a) DESC")
    List<Category> findPopularCategories(@Param("limit") int limit);
    
    // Методы для подсчета
    @Query("SELECT COUNT(a) FROM Article a WHERE a.category.id = :categoryId")
    long countArticlesByCategoryId(@Param("categoryId") Long categoryId);
    
    // Методы для поиска
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Category> searchByName(@Param("query") String query);
    
    // Методы для статистики
    @Query("SELECT COUNT(c) FROM Category c")
    long countAllCategories();
    
    // Методы для получения последних категорий
    List<Category> findTop5ByOrderByIdDesc();
    
    // Метод для проверки использования категории
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Article a WHERE a.category.id = :categoryId")
    boolean isCategoryInUse(@Param("categoryId") Long categoryId);
}