package com.codewiki.codewiki.repository;

import com.codewiki.codewiki.model.Revision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RevisionRepository extends JpaRepository<Revision, Long> {
    
    // Основные методы поиска
    List<Revision> findByArticleIdOrderByCreatedAtDesc(Long articleId);
    Optional<Revision> findFirstByArticleIdOrderByCreatedAtDesc(Long articleId);
    List<Revision> findByEditorIdOrderByCreatedAtDesc(Long editorId); // Изменено с authorId на editorId
    List<Revision> findAllByOrderByCreatedAtDesc();
    
    // Методы для работы с текущими ревизиями
    Optional<Revision> findByArticleIdAndIsCurrentTrue(Long articleId);
    List<Revision> findByIsCurrentTrue();
    
    // Методы для работы с версиями
    Optional<Revision> findTopByArticleIdOrderByVersionNumberDesc(Long articleId);
    
    // Методы изменения данных
    @Modifying
    @Query("UPDATE Revision r SET r.isCurrent = false WHERE r.article.id = :articleId")
    void clearCurrentFlagForArticle(@Param("articleId") Long articleId);
    
    void deleteByArticleId(Long articleId);
    
    // Методы для сложных запросов
    List<Revision> findByEditorIdAndArticleIdOrderByCreatedAtDesc(Long editorId, Long articleId); // Изменено с authorId на editorId
    List<Revision> findByRevisionType(Revision.RevisionType type);
    List<Revision> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Revision> findByChangeSizeGreaterThan(int size);
    
    // Методы подсчета
    long countByArticleId(Long articleId);
    long countByEditorId(Long editorId); // Изменено с authorId на editorId
    long countByRevisionType(Revision.RevisionType type);
    
    // Методы для статистики
    @Query("SELECT MAX(r.versionNumber) FROM Revision r WHERE r.article.id = :articleId")
    Optional<Integer> findMaxVersionNumber(@Param("articleId") Long articleId);
}