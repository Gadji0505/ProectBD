package com.codewiki.codewiki.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users", 
       indexes = {
           @Index(name = "idx_user_username", columnList = "username", unique = true),
           @Index(name = "idx_user_email", columnList = "email", unique = true),
           @Index(name = "idx_user_reputation", columnList = "reputation")
       },
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "username"),
           @UniqueConstraint(columnNames = "email")
       })
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String fullName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 200)
    private String avatarUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonExpired = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonLocked = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime lastLogin;

    @Column(nullable = false)
    @Builder.Default
    private Integer reputation = 0;

    @Column(length = 100)
    private String location;

    @Column(length = 100)
    private String websiteUrl;

    @Column(length = 100)
    private String githubUsername;

    @Column(length = 100)
    private String twitterUsername;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<Article> articles = new HashSet<>();

    @OneToMany(mappedBy = "reportedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<Report> reports = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "deletedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private Set<Comment> deletedComments = new HashSet<>();

    // Конструкторы
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = new HashSet<>();
        this.articles = new HashSet<>();
        this.reports = new HashSet<>();
        this.comments = new HashSet<>();
        this.deletedComments = new HashSet<>();
    }

    // Методы UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return Boolean.TRUE.equals(accountNonExpired);
    }

    @Override
    public boolean isAccountNonLocked() {
        return Boolean.TRUE.equals(accountNonLocked);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return Boolean.TRUE.equals(credentialsNonExpired);
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    // Методы для работы с ролями
    public void addRole(Role role) {
        if (role != null && !this.roles.contains(role)) {
            this.roles.add(role);
            role.getUsers().add(this);
        }
    }

    public void removeRole(Role role) {
        if (role != null && this.roles.contains(role)) {
            this.roles.remove(role);
            role.getUsers().remove(this);
        }
    }

    public boolean hasRole(String roleName) {
        return this.roles.stream()
                .anyMatch(r -> r != null && roleName.equals(r.getName()));
    }

    // Методы для работы со статьями
    public void addArticle(Article article) {
        if (article != null && !this.articles.contains(article)) {
            this.articles.add(article);
            article.setAuthor(this);
        }
    }

    public void removeArticle(Article article) {
        if (article != null && this.articles.contains(article)) {
            this.articles.remove(article);
            article.setAuthor(null);
        }
    }

    // Методы для работы с комментариями
    public void addComment(Comment comment) {
        if (comment != null && !this.comments.contains(comment)) {
            this.comments.add(comment);
            comment.setAuthor(this);
        }
    }

    public void removeComment(Comment comment) {
        if (comment != null && this.comments.contains(comment)) {
            this.comments.remove(comment);
            comment.setAuthor(null);
        }
    }

    // Методы репутации
    public void increaseReputation(int points) {
        if (points > 0) {
            this.reputation += points;
        }
    }

    public void decreaseReputation(int points) {
        if (points > 0) {
            this.reputation = Math.max(0, this.reputation - points);
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
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    // Дополнительные методы
    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    public boolean isModerator() {
        return hasRole("ROLE_MODERATOR");
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    public String getDisplayName() {
        return StringUtils.hasText(fullName) ? fullName : username;
    }

    public String getFormattedCreatedAt() {
        return this.createdAt != null ? 
            this.createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : 
            null;
    }

    // Методы блокировки/разблокировки аккаунта
    public void lockAccount() {
        this.accountNonLocked = false;
    }

    public void unlockAccount() {
        this.accountNonLocked = true;
    }

    // Валидация перед сохранением
    @PrePersist
    @PreUpdate
    private void validate() {
        if (!StringUtils.hasText(username)) {
            throw new IllegalStateException("Username cannot be empty");
        }
        if (!StringUtils.hasText(email)) {
            throw new IllegalStateException("Email cannot be empty");
        }
        if (!StringUtils.hasText(password)) {
            throw new IllegalStateException("Password cannot be empty");
        }
        if (this.reputation < 0) {
            this.reputation = 0;
        }
    }

    // Методы статистики
    public int getArticlesCount() {
        return this.articles != null ? this.articles.size() : 0;
    }

    public int getCommentsCount() {
        return this.comments != null ? this.comments.size() : 0;
    }

    public int getReportsCount() {
        return this.reports != null ? this.reports.size() : 0;
    }

    // Методы для работы с соц. сетями
    public void setGithubProfile(String username) {
        this.githubUsername = username;
    }

    public void setTwitterProfile(String username) {
        this.twitterUsername = username;
    }

    // Методы для работы с аватаром
    public void updateAvatar(String avatarUrl) {
        if (StringUtils.hasText(avatarUrl)) {
            this.avatarUrl = avatarUrl;
        }
    }

    // Методы для работы с профилем
    public void updateProfile(String fullName, String bio, String location, String websiteUrl) {
        this.fullName = fullName;
        this.bio = bio;
        this.location = location;
        this.websiteUrl = websiteUrl;
    }
}