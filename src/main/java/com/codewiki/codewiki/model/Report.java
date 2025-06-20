package com.codewiki.codewiki.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reports")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    public enum Status {
        OPEN, IN_REVIEW, RESOLVED, REJECTED, REOPENED
    }

    public enum ReportType {
        ARTICLE, USER, COMMENT, OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(length = 2000)
    private String description;  // Добавленное поле

    @Column(length = 500)
    private String adminComment;

    @CreationTimestamp
    @Column(name = "report_date", nullable = false, updatable = false)
    private LocalDateTime reportDate;

    @UpdateTimestamp
    @Column(name = "resolution_date")
    private LocalDateTime resolutionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportType reportType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_user_id", nullable = false)
    @ToString.Exclude
    private User reportedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_article_id")
    @ToString.Exclude
    private Article reportedArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id")
    @ToString.Exclude
    private User reportedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_comment_id")
    @ToString.Exclude
    private Comment reportedComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_user_id")
    @ToString.Exclude
    private User resolvedBy;

    // Конструкторы
    public Report(String reason, String description, User reportedBy) {
        this.reason = reason;
        this.description = description;
        this.reportedBy = reportedBy;
        this.status = Status.OPEN;
    }

    // Бизнес-методы
    public void markAsResolved(User resolver, String comment) {
        if (resolver != null) {
            this.status = Status.RESOLVED;
            this.resolvedBy = resolver;
            this.adminComment = comment;
            this.resolutionDate = LocalDateTime.now();
        }
    }

    public void markAsRejected(User resolver, String comment) {
        if (resolver != null) {
            this.status = Status.REJECTED;
            this.resolvedBy = resolver;
            this.adminComment = comment;
            this.resolutionDate = LocalDateTime.now();
        }
    }

    public void reopenReport() {
        if (this.status == Status.RESOLVED || this.status == Status.REJECTED) {
            this.status = Status.REOPENED;
            this.resolutionDate = null;
            this.resolvedBy = null;
        }
    }

    public void putInReview() {
        this.status = Status.IN_REVIEW;
    }

    // Проверки статуса
    public boolean isOpen() {
        return this.status == Status.OPEN || this.status == Status.REOPENED;
    }

    public boolean isResolved() {
        return this.status == Status.RESOLVED;
    }

    public boolean isRejected() {
        return this.status == Status.REJECTED;
    }

    public boolean isInReview() {
        return this.status == Status.IN_REVIEW;
    }

    // equals и hashCode
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? 
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? 
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Report report = (Report) o;
        return getId() != null && Objects.equals(getId(), report.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    // Валидация
    @PrePersist
    @PreUpdate
    private void validate() {
        if (this.reason == null || this.reason.trim().isEmpty()) {
            throw new IllegalStateException("Report reason cannot be empty");
        }
        if (this.reportedBy == null) {
            throw new IllegalStateException("Report must have a reporter");
        }
        if (!hasValidReportTarget()) {
            throw new IllegalStateException("Report must target either an article, user or comment, but not multiple");
        }
        determineReportType();
    }

    private boolean hasValidReportTarget() {
        int targetCount = 0;
        if (reportedArticle != null) targetCount++;
        if (reportedUser != null) targetCount++;
        if (reportedComment != null) targetCount++;
        return targetCount == 1;
    }

    private void determineReportType() {
        if (reportedArticle != null) {
            this.reportType = ReportType.ARTICLE;
        } else if (reportedUser != null) {
            this.reportType = ReportType.USER;
        } else if (reportedComment != null) {
            this.reportType = ReportType.COMMENT;
        } else {
            this.reportType = ReportType.OTHER;
        }
    }

    // Методы для работы с описанием
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }

    // Методы для работы с типом отчета
    public String getTargetDescription() {
        if (reportedArticle != null) {
            return "Article: " + reportedArticle.getTitle();
        } else if (reportedUser != null) {
            return "User: " + reportedUser.getUsername();
        } else if (reportedComment != null) {
            return "Comment by: " + reportedComment.getAuthor().getUsername();
        }
        return "Unknown target";
    }

    // Методы для логирования
    public String getStatusHistory() {
        return String.format("Created: %s | Last updated: %s | Resolved: %s",
                reportDate,
                resolutionDate != null ? resolutionDate : "N/A",
                resolvedBy != null ? resolvedBy.getUsername() : "N/A");
    }

    // Методы для работы с отчетом
    public String getFullReportDetails() {
        return String.format(
            "Report #%d\nType: %s\nStatus: %s\nReason: %s\nDescription: %s\nTarget: %s\nReported by: %s\nDate: %s",
            id,
            reportType,
            status,
            reason,
            hasDescription() ? description : "No description",
            getTargetDescription(),
            reportedBy.getUsername(),
            reportDate
        );
    }
}