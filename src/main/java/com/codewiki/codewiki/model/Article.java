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
@Table(name = "articles", indexes = {
    @Index(name = "idx_article_slug", columnList = "slug", unique = true),
    @Index(name = "idx_article_created_at", columnList = "created_at"),
    @Index(name = "idx_article_author", columnList = "author_id")
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 500)
    private String summary;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @ToString.Exclude
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "article_tags",
        joinColumns = @JoinColumn(name = "article_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Tag> tags = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = true;

    @Column
    private LocalDateTime publishedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<Revision> revisions = new HashSet<>();

    @OneToMany(mappedBy = "reportedArticle", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<Report> reports = new HashSet<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<Comment> comments = new HashSet<>();

    // Методы для работы с тегами
    public void addTag(Tag tag) {
        if (tag != null && !this.tags.contains(tag)) {
            this.tags.add(tag);
            tag.getArticles().add(this);
        }
    }

    public void removeTag(Tag tag) {
        if (tag != null && this.tags.contains(tag)) {
            this.tags.remove(tag);
            tag.getArticles().remove(this);
        }
    }

    public boolean hasTag(Tag tag) {
        return tag != null && this.tags.contains(tag);
    }

    public boolean hasTagWithName(String tagName) {
        return tagName != null && this.tags.stream()
            .anyMatch(tag -> tagName.equalsIgnoreCase(tag.getName()));
    }

    // Методы для работы с ревизиями
    public void addRevision(Revision revision) {
        if (revision != null && !this.revisions.contains(revision)) {
            this.revisions.add(revision);
            revision.setArticle(this);
        }
    }

    public void removeRevision(Revision revision) {
        if (revision != null && this.revisions.contains(revision)) {
            this.revisions.remove(revision);
            revision.setArticle(null);
        }
    }

    public Revision getLatestRevision() {
        return this.revisions.stream()
            .max((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt()))
            .orElse(null);
    }

    // Методы для работы с комментариями
    public void addComment(Comment comment) {
        if (comment != null && !this.comments.contains(comment)) {
            this.comments.add(comment);
            comment.setArticle(this);
        }
    }

    public void removeComment(Comment comment) {
        if (comment != null && this.comments.contains(comment)) {
            this.comments.remove(comment);
            comment.setArticle(null);
        }
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
        Article article = (Article) o;
        return getId() != null && Objects.equals(getId(), article.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    // Методы для генерации и работы с slug
    public void generateSlug() {
        if (StringUtils.hasText(this.title)) {
            this.slug = this.title.trim().toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
        }
    }

    public void generateSummary(int maxLength) {
        if (StringUtils.hasText(this.content)) {
            String plainText = this.content.replaceAll("<[^>]*>", "");
            this.summary = plainText.length() > maxLength ? 
                plainText.substring(0, maxLength) + "..." : plainText;
        }
    }

    // Методы для работы с категориями
    public void setCategory(Category category) {
        if (this.category != null) {
            this.category.getArticles().remove(this);
        }
        this.category = category;
        if (category != null) {
            category.getArticles().add(this);
        }
    }

    // Методы для управления публикацией
    public void publish() {
        this.isPublished = true;
        this.publishedAt = LocalDateTime.now();
    }

    public void unpublish() {
        this.isPublished = false;
        this.publishedAt = null;
    }

    public void markAsFeatured() {
        this.isFeatured = true;
    }

    public void unmarkAsFeatured() {
        this.isFeatured = false;
    }

    // Методы для работы с просмотрами
    public void incrementViewCount() {
        this.viewCount++;
    }

    // Валидация
    @PrePersist
    @PreUpdate
    private void validate() {
        if (!StringUtils.hasText(this.title)) {
            throw new IllegalStateException("Article title cannot be empty");
        }
        if (!StringUtils.hasText(this.content)) {
            throw new IllegalStateException("Article content cannot be empty");
        }
        if (this.author == null) {
            throw new IllegalStateException("Article must have an author");
        }
        if (!StringUtils.hasText(this.slug)) {
            generateSlug();
        }
        if (this.isPublished && this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }

    // Методы для статистики
    public int getCommentsCount() {
        return this.comments != null ? this.comments.size() : 0;
    }

    public int getRevisionsCount() {
        return this.revisions != null ? this.revisions.size() : 0;
    }

    public int getTagsCount() {
        return this.tags != null ? this.tags.size() : 0;
    }

    public int getReportsCount() {
        return this.reports != null ? this.reports.size() : 0;
    }

    // Методы для удобства
    public String getAuthorUsername() {
        return this.author != null ? this.author.getUsername() : null;
    }

    public String getCategoryName() {
        return this.category != null ? this.category.getName() : null;
    }

    public String getFormattedCreatedAt() {
        return this.createdAt != null ? 
            this.createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : 
            null;
    }
}