package com.codewiki.codewiki.controller;

import com.codewiki.codewiki.model.Category;
import com.codewiki.codewiki.service.CategoryService;
import com.codewiki.codewiki.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ArticleService articleService;

    @Autowired
    public CategoryController(CategoryService categoryService, 
                            ArticleService articleService) {
        this.categoryService = categoryService;
        this.articleService = articleService;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories/list";
    }

    @GetMapping("/view/{slug}")
    public String viewCategory(@PathVariable String slug, 
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Category category = categoryService.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        
        model.addAttribute("category", category);
        model.addAttribute("articles", articleService.getArticlesByCategoryId(category.getId()));
        return "categories/view";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createCategory(@ModelAttribute Category category,
                               RedirectAttributes redirectAttributes) {
        try {
            categoryService.saveCategory(category);
            redirectAttributes.addFlashAttribute("message", "Category created successfully");
            return "redirect:/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/categories/create";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.getCategoryById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category id"));
            model.addAttribute("category", category);
            return "categories/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/categories";
        }
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateCategory(@PathVariable Long id,
                               @ModelAttribute Category category,
                               RedirectAttributes redirectAttributes) {
        try {
            category.setId(id);
            categoryService.saveCategory(category);
            redirectAttributes.addFlashAttribute("message", "Category updated successfully");
            return "redirect:/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/categories/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCategory(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("message", "Category deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories";
    }
}