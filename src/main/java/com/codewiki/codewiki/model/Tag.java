package com.codewiki.codewiki.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tags", 
       indexes = {
           @Index(name = "idx_tag_name", columnList = "name", unique = true),
           @Index(name = "idx_tag_slug", columnList = "slug", unique = true),
           @Index(name = "idx_tag_featured", columnList = "isFeatured")
       })
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(length = 120, unique = true)
    private String slug;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer usageCount = 0;

    @Column(length = 50)
    private String iconClass;

    @Column(length = 7)
    private String colorCode;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<Article> articles = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Конструкторы
    public Tag(String name) {
        this.name = name;
        this.articles = new HashSet<>();
        this.isFeatured = false;
        this.usageCount = 0;
        generateSlug();
    }

    public Tag(String name, String description) {
        this(name);
        this.description = description;
    }

    // Методы для работы со статьями
    public void addArticle(Article article) {
        if (article != null && !this.articles.contains(article)) {
            this.articles.add(article);
            article.getTags().add(this);
            incrementUsageCount();
        }
    }

    public void removeArticle(Article article) {
        if (article != null && this.articles.contains(article)) {
            this.articles.remove(article);
            article.getTags().remove(this);
            decrementUsageCount();
        }
    }

    // Методы для работы с usageCount
    public void incrementUsageCount() {
        this.usageCount++;
    }

    public void decrementUsageCount() {
        this.usageCount = Math.max(0, this.usageCount - 1);
    }

    // Метод генерации слага
    public void generateSlug() {
        if (StringUtils.hasText(this.name)) {
            this.slug = this.name.trim().toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-+", "-");
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
        Tag tag = (Tag) o;
        return getId() != null && Objects.equals(getId(), tag.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    // Методы для работы с featured
    public boolean isFeatured() {
        return Boolean.TRUE.equals(this.isFeatured);
    }

    public void markAsFeatured() {
        this.isFeatured = true;
    }

    public void unmarkAsFeatured() {
        this.isFeatured = false;
    }

    // Методы статистики
    public int getArticlesCount() {
        return this.articles != null ? this.articles.size() : 0;
    }

    public String getFormattedCreatedAt() {
        return this.createdAt != null ? 
            this.createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : 
            null;
    }

    // Методы для работы с внешним видом
    public void setAppearance(String iconClass, String colorCode) {
        this.iconClass = iconClass;
        if (colorCode != null && colorCode.matches("^#[0-9a-fA-F]{6}$")) {
            this.colorCode = colorCode;
        }
    }

    // Валидация перед сохранением
    @PrePersist
    @PreUpdate
    private void validate() {
        if (!StringUtils.hasText(this.name)) {
            throw new IllegalStateException("Tag name cannot be empty");
        }
        if (this.slug == null || this.slug.trim().isEmpty()) {
            generateSlug();
        }
        if (this.usageCount < 0) {
            this.usageCount = 0;
        }
    }

    // Методы для работы с описанием
    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    // Методы для DTO
    public String getDisplayName() {
        return StringUtils.hasText(this.name) ? this.name : "Unnamed Tag";
    }
}