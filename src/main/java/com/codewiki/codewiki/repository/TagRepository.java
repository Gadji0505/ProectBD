package com.codewiki.codewiki.repository;

import com.codewiki.codewiki.model.Tag;
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
public interface TagRepository extends JpaRepository<Tag, Long> {

    // Основные методы поиска
    Optional<Tag> findByName(String name);
    Optional<Tag> findBySlug(String slug);
    Optional<Tag> findByNameIgnoreCase(String name);
    
    // Методы для пагинации и сортировки
    List<Tag> findTop10ByOrderByNameAsc();
    Page<Tag> findAll(Pageable pageable);
    
    // Методы проверки существования
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    
    // Методы для работы с коллекциями
    List<Tag> findAllByIdIn(Set<Long> ids); // Исправленный метод
    
    // Методы для популярных тегов
    @Query("SELECT t FROM Tag t LEFT JOIN t.articles a GROUP BY t.id ORDER BY COUNT(a) DESC")
    List<Tag> findPopularTags(@Param("limit") int limit);
    
    // Методы для поиска
    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Tag> searchByName(@Param("query") String query);
    
    // Методы для статистики
    @Query("SELECT COUNT(t) FROM Tag t")
    long countAllTags();
    
    @Query("SELECT COUNT(a) FROM Article a JOIN a.tags t WHERE t.id = :tagId")
    long countArticlesByTagId(@Param("tagId") Long tagId);
    
    // Метод для поиска или создания тега
    default Tag findOrCreateByName(String name) {
        return findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(name);
                    return save(newTag);
                });
    }
    
    // Метод для поиска тегов по списку имен
    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) IN :names")
    List<Tag> findByNamesIgnoreCase(@Param("names") Set<String> names);
}