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
@Table(name = "categories", indexes = {
    @Index(name = "idx_category_name", columnList = "name"),
    @Index(name = "idx_category_slug", columnList = "slug"),
    @Index(name = "idx_category_featured", columnList = "isFeatured")
})
@Getter
@Setter
@ToString(exclude = "articles")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Column(length = 500)
    private String description;

    @Column(length = 200)
    private String iconUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Article> articles = new HashSet<>();

    // Конструкторы
    public Category(String name) {
        this.name = name;
        this.slug = generateSlugFromName();
    }

    public Category(String name, String description) {
        this(name);
        this.description = description;
    }

    // Методы для работы со слагом
    public void generateSlug() {
        if (!StringUtils.hasText(this.slug)) {
            this.slug = generateSlugFromName();
        }
    }

    private String generateSlugFromName() {
        if (!StringUtils.hasText(this.name)) {
            throw new IllegalStateException("Category name cannot be empty");
        }
        return this.name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    // Методы для работы со статьями
    public void addArticle(Article article) {
        articles.add(article);
        article.setCategory(this);
    }

    public void removeArticle(Article article) {
        articles.remove(article);
        article.setCategory(null);
    }

    // Валидация
    @PrePersist
    @PreUpdate
    private void validate() {
        if (!StringUtils.hasText(this.name)) {
            throw new IllegalStateException("Category name cannot be empty");
        }
        generateSlug();
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
        Category category = (Category) o;
        return getId() != null && Objects.equals(getId(), category.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    // Дополнительные методы
    public int getArticlesCount() {
        return articles.size();
    }

    public boolean isFeatured() {
        return Boolean.TRUE.equals(isFeatured);
    }

    public void toggleFeatured() {
        this.isFeatured = !this.isFeatured;
    }
}