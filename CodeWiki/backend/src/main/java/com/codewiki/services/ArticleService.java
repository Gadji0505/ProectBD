package com.codewiki.services;

import com.codewiki.dto.ArticleRequest;
import com.codewiki.models.*;
import com.codewiki.repositories.ArticleRepository;
import com.codewiki.repositories.TagRepository;
import com.codewiki.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepo;
    private final UserRepository userRepo;
    private final TagRepository tagRepo;

    public Page<Article> getAllArticles(Pageable pageable) {
        return articleRepo.findByStatus(ArticleStatus.PUBLISHED, pageable);
    }

    public Article getArticleBySlug(String slug) {
        return articleRepo.findBySlug(slug)
            .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    @Transactional
    public Article createArticle(ArticleRequest request, Long authorId) {
        User author = userRepo.findById(authorId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setSlug(generateSlug(request.getTitle()));
        article.setContent(request.getContent());
        article.setAuthor(author);
        article.setStatus(ArticleStatus.PUBLISHED);

        // Добавление тегов
        Set<Tag> tags = request.getTags().stream()
            .map(tagName -> tagRepo.findByName(tagName)
                .orElseGet(() -> tagRepo.save(new Tag(tagName)))
            .collect(Collectors.toSet());
        article.setTags(tags);

        return articleRepo.save(article);
    }

    private String generateSlug(String title) {
        return title.toLowerCase().replace(" ", "-");
    }
}