package com.codewiki.codewiki.controller;

import com.codewiki.codewiki.service.ArticleService;
import com.codewiki.codewiki.service.CategoryService;
import com.codewiki.codewiki.service.ReportService;
import com.codewiki.codewiki.service.TagService;
import com.codewiki.codewiki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final ReportService reportService;

    @Autowired
    public AdminController(UserService userService, ArticleService articleService,
                           CategoryService categoryService, TagService tagService,
                           ReportService reportService) {
        this.userService = userService;
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // ИЗМЕНЕНО: Вызов метода теперь 'getAllUsers()'
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalArticles", articleService.getAllArticles().size());
        model.addAttribute("totalCategories", categoryService.getAllCategories().size());
        model.addAttribute("totalTags", tagService.getAllTags().size());
        model.addAttribute("latestReports", reportService.getLatestReports(5));
        return "admin/dashboard";
    }
}