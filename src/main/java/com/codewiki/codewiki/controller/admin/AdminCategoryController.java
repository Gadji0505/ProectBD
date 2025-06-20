package com.codewiki.codewiki.controller.admin;

import com.codewiki.codewiki.model.Category;
import com.codewiki.codewiki.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @Autowired
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories";
    }

    @GetMapping("/new")
    public String showCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category_form";
    }

    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Category not found");
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category.get());
        return "admin/category_form";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute Category category, 
                             RedirectAttributes redirectAttributes) {
        try {
            // Проверка уникальности имени
            Optional<Category> existingByName = categoryService.findByName(category.getName());
            if (existingByName.isPresent() && !existingByName.get().getId().equals(category.getId())) {
                redirectAttributes.addFlashAttribute("error", "Category with this name already exists");
                return "redirect:/admin/categories/new";
            }

            // Проверка уникальности slug
            Optional<Category> existingBySlug = categoryService.findBySlug(category.getSlug());
            if (existingBySlug.isPresent() && !existingBySlug.get().getId().equals(category.getId())) {
                redirectAttributes.addFlashAttribute("error", "Category with this slug already exists");
                return "redirect:/admin/categories/new";
            }

            categoryService.saveCategory(category);
            redirectAttributes.addFlashAttribute("success", "Category saved successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Category deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}