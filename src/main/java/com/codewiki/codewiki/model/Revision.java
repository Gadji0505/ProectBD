package com.codewiki.codewiki.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "revisions", indexes = {
    @Index(name = "idx_revision_article", columnList = "article_id"),
    @Index(name = "idx_revision_editor", columnList = "editor_id"),
    @Index(name = "idx_revision_current", columnList = "isCurrent"),
    @Index(name = "idx_revision_created", columnList = "created_at"),
    @Index(name = "idx_revision_version", columnList = "article_id,versionNumber")
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Revision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    @ToString.Exclude
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id", nullable = false)
    @ToString.Exclude
    private User editor;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 500)
    private String editSummary;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private RevisionType revisionType = RevisionType.CONTENT_UPDATE;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isCurrent = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer versionNumber = 1;

    @Column(length = 50)
    private String ipAddress;

    @Column(length = 100)
    private String userAgent;

    @Column(nullable = false)
    @Builder.Default
    private Integer wordCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer changeSize = 0;

    @Column(length = 50)
    private String previousVersionHash;

    public enum RevisionType {
        INITIAL,
        CONTENT_UPDATE,
        TITLE_UPDATE,
        MAJOR_EDIT,
        MINOR_EDIT,
        REVERT,
        CONTENT_REORGANIZATION,
        FORMATTING,
        TRANSLATION,
        METADATA_UPDATE,
        IMAGE_UPDATE,
        CODE_UPDATE
    }

    public Revision(Article article, User editor, String title, String content) {
        this.article = article;
        this.editor = editor;
        this.title = title;
        this.content = content;
        this.revisionType = RevisionType.INITIAL;
        this.isCurrent = true;
        calculateWordCount();
    }

    public void markAsCurrent() {
        this.isCurrent = true;
        if (this.article != null) {
            this.article.getRevisions().forEach(rev -> {
                if (!rev.equals(this)) {
                    rev.unmarkAsCurrent();
                }
            });
        }
    }

    public void unmarkAsCurrent() {
        this.isCurrent = false;
    }

    public boolean isInitialRevision() {
        return revisionType == RevisionType.INITIAL;
    }

    public boolean isContentUpdate() {
        return revisionType == RevisionType.CONTENT_UPDATE || 
               revisionType == RevisionType.MAJOR_EDIT || 
               revisionType == RevisionType.MINOR_EDIT ||
               revisionType == RevisionType.CODE_UPDATE;
    }

    public boolean isTitleUpdate() {
        return revisionType == RevisionType.TITLE_UPDATE;
    }

    public boolean isVisualUpdate() {
        return revisionType == RevisionType.IMAGE_UPDATE || 
               revisionType == RevisionType.FORMATTING;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? 
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? 
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Revision revision = (Revision) o;
        return getId() != null && Objects.equals(getId(), revision.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (this.article == null) {
            throw new IllegalStateException("Revision must belong to an article");
        }
        if (this.editor == null) {
            throw new IllegalStateException("Revision must have an editor");
        }
        if (!StringUtils.hasText(this.title)) {
            throw new IllegalStateException("Revision title cannot be empty");
        }
        if (!StringUtils.hasText(this.content)) {
            throw new IllegalStateException("Revision content cannot be empty");
        }
        if (this.versionNumber == null || this.versionNumber < 1) {
            throw new IllegalStateException("Version number must be positive");
        }
        if (this.isCurrent && this.article != null) {
            this.article.getRevisions().forEach(r -> {
                if (!r.equals(this) && r.getIsCurrent()) {
                    r.setIsCurrent(false);
                }
            });
        }
        calculateWordCount();
    }

    public String getContentPreview(int maxLength) {
        if (!StringUtils.hasText(this.content)) return "";
        String plainText = this.content.replaceAll("<[^>]*>", "");
        return plainText.length() > maxLength 
                ? plainText.substring(0, maxLength) + "..."
                : plainText;
    }

    public String getShortDescription() {
        return String.format("v%d | %s | %s | %d words", 
                versionNumber, 
                revisionType.toString().toLowerCase().replace("_", " "),
                getEditorUsername(),
                wordCount);
    }

    public boolean isMajorChange() {
        return revisionType == RevisionType.MAJOR_EDIT || 
               revisionType == RevisionType.CONTENT_REORGANIZATION ||
               revisionType == RevisionType.TRANSLATION ||
               revisionType == RevisionType.CODE_UPDATE;
    }

    public void setIpAddress(String ipAddress) {
        if (ipAddress != null && ipAddress.length() <= 50) {
            this.ipAddress = ipAddress;
        }
    }

    public void setUserAgent(String userAgent) {
        if (userAgent != null && userAgent.length() <= 100) {
            this.userAgent = userAgent;
        }
    }

    private void calculateWordCount() {
        if (StringUtils.hasText(this.content)) {
            String plainText = this.content.replaceAll("<[^>]*>", "");
            this.wordCount = plainText.trim().isEmpty() ? 0 : 
                plainText.split("\\s+").length;
        } else {
            this.wordCount = 0;
        }
    }

    public void calculateChangeSize(Revision previousRevision) {
        int currentLength = StringUtils.hasText(this.content) ? this.content.length() : 0;
        int prevLength = (previousRevision != null && StringUtils.hasText(previousRevision.content)) 
            ? previousRevision.content.length() 
            : 0;
        this.changeSize = Math.abs(currentLength - prevLength);
    }

    public void generateVersionHash() {
        if (!StringUtils.hasText(this.content)) {
            throw new IllegalStateException("Cannot generate hash for empty content");
        }
        this.previousVersionHash = Integer.toHexString(this.content.hashCode());
    }

    public String getFormattedCreatedAt() {
        return this.createdAt != null ? 
            this.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : 
            "N/A";
    }

    public String getEditorUsername() {
        return this.editor != null ? this.editor.getUsername() : "unknown";
    }

    public boolean isSameContent(Revision other) {
        if (other == null) return false;
        return Objects.equals(this.content, other.content) && 
               Objects.equals(this.title, other.title);
    }

    @Transient
    public String getChangeLog(Revision previous) {
        if (previous == null) return "Initial revision";
        
        StringBuilder changes = new StringBuilder();
        if (!Objects.equals(this.title, previous.title)) {
            changes.append(String.format("Title changed from '%s' to '%s'", 
                previous.title, this.title));
        }
        
        if (!Objects.equals(this.content, previous.content)) {
            if (!changes.isEmpty()) changes.append("\n");
            changes.append("Content updated");
        }
        
        return changes.isEmpty() ? "Minor formatting changes" : changes.toString();
    }
}