package com.codewiki.codewiki.repository;

import com.codewiki.codewiki.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Основные методы поиска
    List<Report> findAllByOrderByReportDateDesc();
    List<Report> findByStatusOrderByReportDateDesc(Report.Status status);
    List<Report> findByReportedArticleId(Long articleId);
    List<Report> findByReportedUserId(Long userId);
    
    // Методы с комбинированными условиями
    List<Report> findByStatusAndReportedArticleId(Report.Status status, Long articleId);
    List<Report> findByStatusAndReportedUserId(Report.Status status, Long userId);
    
    // Методы для пагинации
    Page<Report> findAll(Pageable pageable);
    Page<Report> findByStatus(Report.Status status, Pageable pageable);
    
    // Добавлен новый метод для поиска без пагинации
    List<Report> findByStatus(Report.Status status);
    
    // Методы подсчета
    long countByStatus(Report.Status status);
    long countByReportedArticleId(Long articleId);
    long countByReportedUserId(Long userId);
    
    // Методы для поиска по дате
    List<Report> findByReportDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Report> findByStatusAndReportDateBetween(Report.Status status, LocalDateTime startDate, LocalDateTime endDate);
    
    // Методы для администратора
    @Query("SELECT r FROM Report r WHERE " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:articleId IS NULL OR r.reportedArticle.id = :articleId) AND " +
           "(:userId IS NULL OR r.reportedUser.id = :userId) " +
           "ORDER BY r.reportDate DESC")
    Page<Report> findReportsForAdmin(
            @Param("status") Report.Status status,
            @Param("articleId") Long articleId,
            @Param("userId") Long userId,
            Pageable pageable);
    
    // Методы для статистики
    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = 'RESOLVED'")
    long countResolvedReports();
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = 'PENDING'")
    long countPendingReports();
    
    // Исправленный метод для проверки существования
    boolean existsByReportedArticleIdAndReportedByIdAndStatus(
            Long articleId, 
            Long reportedById, 
            Report.Status status);
}