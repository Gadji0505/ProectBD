package com.codewiki.codewiki.controller;

import com.codewiki.codewiki.model.*;
import com.codewiki.codewiki.service.*;
import com.codewiki.codewiki.util.MarkdownUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final UserService userService;
    private final CommentService commentService;

    @Autowired
    public ArticleController(ArticleService articleService,
                           CategoryService categoryService,
                           TagService tagService,
                           UserService userService,
                           CommentService commentService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping
    public String listArticles(Model model) {
        model.addAttribute("articles", articleService.getAllArticles());
        return "articles/list";
    }

    @GetMapping("/view/{slug}")
    public String viewArticle(@PathVariable String slug, 
                            Model model, 
                            RedirectAttributes redirectAttributes) {
        try {
            Article article = articleService.getArticleBySlug(slug)
                    .orElseThrow(() -> new IllegalArgumentException("Article not found"));
            
            model.addAttribute("article", article);
            model.addAttribute("htmlContent", MarkdownUtil.markdownToHtml(article.getContent()));
            model.addAttribute("comments", commentService.getCommentsByArticleId(article.getId()));
            model.addAttribute("newComment", new Comment());
            return "articles/view";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/articles";
        }
    }

    @GetMapping("/create")
    public String createArticleForm(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("allTags", tagService.getAllTags());
        return "articles/create_edit";
    }

    @PostMapping("/create")
    public String createArticle(@ModelAttribute Article article,
                              @RequestParam(required = false) Long categoryId,
                              @RequestParam(required = false) String tags,
                              RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        try {
            Set<String> tagNames = parseTags(tags);
            Article savedArticle = articleService.saveArticle(article, auth.getName(), categoryId, tagNames);
            redirectAttributes.addFlashAttribute("success", "Article created successfully");
            return "redirect:/articles/view/" + savedArticle.getSlug();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/articles/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String editArticleForm(@PathVariable Long id,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            Article article = articleService.getArticleById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Article not found"));
            
            model.addAttribute("article", article);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("allTags", tagService.getAllTags());
            model.addAttribute("currentTags", article.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.joining(", ")));
            return "articles/create_edit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/articles";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateArticle(@PathVariable Long id,
                              @ModelAttribute Article article,
                              @RequestParam(required = false) Long categoryId,
                              @RequestParam(required = false) String tags,
                              RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        try {
            Set<String> tagNames = parseTags(tags);
            Article updatedArticle = articleService.updateArticle(id, article, auth.getName(), categoryId, tagNames);
            redirectAttributes.addFlashAttribute("success", "Article updated successfully");
            return "redirect:/articles/view/" + updatedArticle.getSlug();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/articles/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            articleService.deleteArticle(id);
            redirectAttributes.addFlashAttribute("success", "Article deleted successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/articles";
    }

    private Set<String> parseTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}