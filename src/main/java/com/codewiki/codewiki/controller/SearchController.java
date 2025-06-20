package com.codewiki.codewiki.controller;

import com.codewiki.codewiki.model.Article;
import com.codewiki.codewiki.service.ArticleService;
import com.codewiki.codewiki.util.MarkdownUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final ArticleService articleService;
    private final MarkdownUtil markdownUtil; // Для корректного отображения сниппетов

    @Autowired
    public SearchController(ArticleService articleService, MarkdownUtil markdownUtil) {
        this.articleService = articleService;
        this.markdownUtil = markdownUtil;
    }

    @GetMapping
    public String search(@RequestParam(value = "query", required = false) String query, Model model) {
        if (query != null && !query.trim().isEmpty()) {
            List<Article> searchResults = articleService.searchArticles(query.trim());
            model.addAttribute("articles", searchResults);
            model.addAttribute("query", query.trim());
        }
        // Если запроса нет, просто отображаем пустую страницу поиска или предлагаем ввести запрос
        return "search";
    }
}