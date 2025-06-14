package com.codewiki.controllers;

import com.codewiki.models.Article;
import com.codewiki.services.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public Page<Article> getAllArticles(Pageable pageable) {
        return articleService.findAll(pageable);
    }

    @GetMapping("/{slug}")
    public Article getArticleBySlug(@PathVariable String slug) {
        return articleService.findBySlug(slug);
    }

    @PostMapping
    public Article createArticle(@RequestBody Article article) {
        return articleService.save(article);
    }

    @PutMapping("/{id}")
    public Article updateArticle(@PathVariable Long id, @RequestBody Article article) {
        return articleService.update(id, article);
    }

    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable Long id) {
        articleService.delete(id);
    }

    @PostMapping("/{id}/vote")
    public void vote(@PathVariable Long id, @RequestParam boolean isUpvote) {
        articleService.vote(id, isUpvote);
    }
}