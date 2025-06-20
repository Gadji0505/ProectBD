package com.codewiki.codewiki.service;

import com.codewiki.codewiki.model.Article;
import com.codewiki.codewiki.model.Category;
import com.codewiki.codewiki.model.Revision;
import com.codewiki.codewiki.model.Tag;
import com.codewiki.codewiki.model.User;
import com.codewiki.codewiki.repository.ArticleRepository;
import com.codewiki.codewiki.repository.UserRepository;
import com.codewiki.codewiki.util.MarkdownUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TagService tagService;
    private final CategoryService categoryService;
    private final RevisionService revisionService;
    private final UserRepository userRepository;

    @Autowired
    public ArticleService(
            ArticleRepository articleRepository,
            TagService tagService,
            CategoryService categoryService,
            RevisionService revisionService,
            UserRepository userRepository
    ) {
        this.articleRepository = articleRepository;
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.revisionService = revisionService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Article> getAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Article> getArticleBySlug(String slug) {
        return articleRepository.findBySlug(slug);
    }

    @Transactional(readOnly = true)
    public List<Article> getArticlesByAuthor(String username) {
        return articleRepository.findByAuthorUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Article> getArticlesByCategory(Long categoryId) {
        return articleRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<Article> getArticlesByCategoryId(Long categoryId) {
        return articleRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<Article> getArticlesByTagId(Long tagId) {
        return articleRepository.findByTagId(tagId);
    }

    @Transactional(readOnly = true)
    public Page<Article> getLatestArticles(Pageable pageable) {
        return articleRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public List<Article> getPopularArticles(int limit) {
        return articleRepository.findPopularArticles(limit);
    }

    @Transactional(readOnly = true)
    public long getTotalArticlesCount() {
        return articleRepository.count();
    }

    @Transactional
    public Article saveArticle(Article article, String authorUsername, Long categoryId, Set<String> tagNames) {
        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new IllegalArgumentException("Author not found: " + authorUsername));
        
        article.setAuthor(author);

        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
            article.setCategory(category);
        } else {
            article.setCategory(null);
        }

        Set<Tag> tags = new HashSet<>();
        if (tagNames != null) {
            for (String tagName : tagNames) {
                Tag tag = tagService.findOrCreateTag(tagName);
                tags.add(tag);
            }
        }
        article.setTags(tags);

        String baseSlug = MarkdownUtil.toSlug(article.getTitle());
        article.setSlug(generateUniqueSlug(baseSlug));

        LocalDateTime now = LocalDateTime.now();
        article.setCreatedAt(now);
        article.setUpdatedAt(now);

        Article savedArticle = articleRepository.save(article);
        createInitialRevision(savedArticle, author, now);

        return savedArticle;
    }

    @Transactional
    public Article updateArticle(Long id, Article updatedArticle, String editorUsername, Long categoryId, Set<String> tagNames) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id: " + id));

        String oldTitle = existingArticle.getTitle();
        String oldContent = existingArticle.getContent();

        existingArticle.setTitle(updatedArticle.getTitle());
        existingArticle.setContent(updatedArticle.getContent());

        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
            existingArticle.setCategory(category);
        } else {
            existingArticle.setCategory(null);
        }

        Set<Tag> tags = new HashSet<>();
        if (tagNames != null) {
            for (String tagName : tagNames) {
                Tag tag = tagService.findOrCreateTag(tagName);
                tags.add(tag);
            }
        }
        existingArticle.setTags(tags);

        if (!oldTitle.equals(updatedArticle.getTitle())) {
            String newBaseSlug = MarkdownUtil.toSlug(updatedArticle.getTitle());
            existingArticle.setSlug(generateUniqueSlug(newBaseSlug));
        }

        existingArticle.setUpdatedAt(LocalDateTime.now());
        Article savedUpdatedArticle = articleRepository.save(existingArticle);

        User editor = userRepository.findByUsername(editorUsername).orElse(null);

        if (!oldTitle.equals(savedUpdatedArticle.getTitle()) || !oldContent.equals(savedUpdatedArticle.getContent())) {
            createRevision(savedUpdatedArticle, editor, savedUpdatedArticle.getUpdatedAt());
        }

        return savedUpdatedArticle;
    }

    @Transactional
    public Article createArticle(Article article, String authorUsername) {
        return saveArticle(article, authorUsername, 
                article.getCategory() != null ? article.getCategory().getId() : null, 
                article.getTags() != null ? 
                        article.getTags().stream().map(Tag::getName).collect(Collectors.toSet()) : 
                        null);
    }

    @Transactional
    public void deleteArticle(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new IllegalArgumentException("Article not found with id: " + id);
        }
        revisionService.deleteRevisionsByArticleId(id);
        articleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Article> findByTagsIds(Set<Long> tagIds) {
        return articleRepository.findByTagsIds(tagIds);
    }

    @Transactional(readOnly = true)
    public Page<Article> findComplexSearch(String query, Long categoryId, Set<Long> tagIds, Pageable pageable) {
        if (query == null && categoryId == null && (tagIds == null || tagIds.isEmpty())) {
            return articleRepository.findAll(pageable);
        }
        return articleRepository.complexSearch(query, categoryId, tagIds, pageable);
    }

    @Transactional(readOnly = true)
    public List<Article> searchArticles(String query) {
        return articleRepository.searchByTitleOrContent(query);
    }

    private void processTags(Article article) {
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            Set<Long> tagIds = article.getTags().stream()
                    .map(Tag::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            Set<Tag> existingTags = new HashSet<>(tagService.findAllById(tagIds));
            article.setTags(existingTags);
        } else {
            article.setTags(new HashSet<>());
        }
    }

    private String generateUniqueSlug(String baseSlug) {
        String finalSlug = baseSlug;
        int counter = 1;
        while (articleRepository.findBySlug(finalSlug).isPresent()) {
            finalSlug = baseSlug + "-" + counter++;
        }
        return finalSlug;
    }

    private void createInitialRevision(Article article, User author, LocalDateTime createdAt) {
        Revision initialRevision = new Revision();
        initialRevision.setArticle(article);
        initialRevision.setTitle(article.getTitle());
        initialRevision.setContent(article.getContent());
        initialRevision.setEditor(author);
        initialRevision.setCreatedAt(createdAt);
        initialRevision.setVersionNumber(1);
        revisionService.saveRevision(initialRevision);
    }

    private void createRevision(Article article, User editor, LocalDateTime createdAt) {
        Optional<Revision> lastRevision = revisionService.findLatestByArticleId(article.getId());
        int newVersion = lastRevision.map(rev -> rev.getVersionNumber() + 1).orElse(1);

        Revision newRevision = new Revision();
        newRevision.setArticle(article);
        newRevision.setTitle(article.getTitle());
        newRevision.setContent(article.getContent());
        newRevision.setEditor(editor);
        newRevision.setCreatedAt(createdAt);
        newRevision.setVersionNumber(newVersion);
        revisionService.saveRevision(newRevision);
    }
}