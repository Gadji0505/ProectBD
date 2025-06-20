package com.codewiki.codewiki.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    @ToString.Exclude
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @ToString.Exclude
    private User author;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    @ToString.Exclude
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<Comment> replies = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean isEdited = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by_id")
    @ToString.Exclude
    private User deletedBy;

    // Методы для работы с иерархией комментариев
    public void addReply(Comment reply) {
        if (reply != null && !this.replies.contains(reply)) {
            this.replies.add(reply);
            reply.setParentComment(this);
            reply.setArticle(this.article);
        }
    }

    public void removeReply(Comment reply) {
        if (reply != null && this.replies.contains(reply)) {
            this.replies.remove(reply);
            reply.setParentComment(null);
        }
    }

    // Методы для управления состоянием комментария
    public void markAsEdited() {
        this.isEdited = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deleteComment(User deletedByUser) {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedByUser;
        
        // Рекурсивное удаление всех ответов
        if (this.replies != null) {
            this.replies.forEach(reply -> reply.deleteComment(deletedByUser));
        }
    }

    public void restoreComment() {
        this.isDeleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
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
        Comment comment = (Comment) o;
        return getId() != null && Objects.equals(getId(), comment.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    // Методы проверки состояния
    public boolean isReply() {
        return this.parentComment != null;
    }

    public boolean hasReplies() {
        return this.replies != null && !this.replies.isEmpty();
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(this.isDeleted);
    }

    // Методы для работы с контентом
    public void updateContent(String newContent) {
        if (StringUtils.hasText(newContent) && !newContent.equals(this.content)) {
            this.content = newContent;
            markAsEdited();
        }
    }

    // Методы для работы со статьей и автором
    public void setArticle(Article article) {
        if (article != null) {
            this.article = article;
            // Обновляем статью для всех ответов
            if (this.replies != null) {
                this.replies.forEach(reply -> reply.setArticle(article));
            }
        }
    }

    // Валидация перед сохранением/обновлением
    @PrePersist
    @PreUpdate
    private void validate() {
        if (!StringUtils.hasText(this.content)) {
            throw new IllegalStateException("Comment content cannot be empty");
        }
        if (this.author == null) {
            throw new IllegalStateException("Comment must have an author");
        }
        if (this.article == null) {
            throw new IllegalStateException("Comment must belong to an article");
        }
        if (this.isDeleted && this.deletedBy == null) {
            throw new IllegalStateException("Deleted comment must have a user who deleted it");
        }
    }

    // Методы для получения информации
    public String getAuthorUsername() {
        return this.author != null ? this.author.getUsername() : null;
    }

    public String getArticleTitle() {
        return this.article != null ? this.article.getTitle() : null;
    }

    public Long getArticleId() {
        return this.article != null ? this.article.getId() : null;
    }

    // Метод для форматированного вывода времени
    public String getFormattedCreatedAt() {
        return this.createdAt != null ? 
               this.createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : 
               null;
    }
}