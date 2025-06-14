package com.codewiki.repositories;

import com.codewiki.models.Report;
import com.codewiki.models.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Найти все открытые жалобы
    List<Report> findByStatus(ReportStatus status);

    // Найти жалобы на конкретную статью/комментарий/пользователя
    List<Report> findByTargetTypeAndTargetId(String targetType, Long targetId);
}