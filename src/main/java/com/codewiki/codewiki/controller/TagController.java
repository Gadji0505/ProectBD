package com.codewiki.codewiki.controller;

import com.codewiki.codewiki.model.Tag;
import com.codewiki.codewiki.service.TagService;
import com.codewiki.codewiki.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;
    private final ArticleService articleService;

    @Autowired
    public TagController(TagService tagService, ArticleService articleService) {
        this.tagService = tagService;
        this.articleService = articleService;
    }

    @GetMapping
    public String listTags(Model model) {
        model.addAttribute("tags", tagService.getAllTags());
        return "tags/list";
    }

    @GetMapping("/view/{id}")
    public String viewTag(@PathVariable Long id,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        try {
            Tag tag = tagService.getTagById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
            
            model.addAttribute("tag", tag);
            model.addAttribute("articles", articleService.getArticlesByTagId(id));
            return "tags/view";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tags";
        }
    }
}