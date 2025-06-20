package com.codewiki.codewiki.controller;

import com.codewiki.codewiki.model.Article;
import com.codewiki.codewiki.model.Report;
import com.codewiki.codewiki.model.User;
import com.codewiki.codewiki.service.ArticleService;
import com.codewiki.codewiki.service.ReportService;
import com.codewiki.codewiki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;
    private final ArticleService articleService;

    @Autowired
    public ReportController(ReportService reportService, 
                          UserService userService, 
                          ArticleService articleService) {
        this.reportService = reportService;
        this.userService = userService;
        this.articleService = articleService;
    }

    @GetMapping("/article/{articleId}")
    public String showReportArticleForm(@PathVariable Long articleId, 
                                      Model model, 
                                      RedirectAttributes redirectAttributes) {
        Article article = articleService.getArticleById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));
        
        model.addAttribute("report", new Report());
        model.addAttribute("reportedArticle", article);
        return "report/report_article";
    }

    @PostMapping("/article/{articleId}")
    public String reportArticle(@PathVariable Long articleId,
                              @ModelAttribute Report report,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to submit a report");
            return "redirect:/login";
        }

        User reportedBy = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Article reportedArticle = articleService.getArticleById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));

        report.setReportedBy(reportedBy);
        report.setReportedArticle(reportedArticle);
        report.setReportDate(LocalDateTime.now());
        report.setStatus(Report.Status.OPEN);

        reportService.saveReport(report);
        redirectAttributes.addFlashAttribute("message", "Report submitted successfully");
        return "redirect:/articles/view/" + reportedArticle.getSlug();
    }

    @GetMapping("/user/{userId}")
    public String showReportUserForm(@PathVariable Long userId,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        User reportedUser = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        model.addAttribute("report", new Report());
        model.addAttribute("reportedUser", reportedUser);
        return "report/report_user";
    }

    @PostMapping("/user/{userId}")
    public String reportUser(@PathVariable Long userId,
                           @ModelAttribute Report report,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to submit a report");
            return "redirect:/login";
        }

        User reportedBy = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User reportedUser = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        report.setReportedBy(reportedBy);
        report.setReportedUser(reportedUser);
        report.setReportDate(LocalDateTime.now());
        report.setStatus(Report.Status.OPEN);

        reportService.saveReport(report);
        redirectAttributes.addFlashAttribute("message", "Report submitted successfully");
        return "redirect:/users/" + reportedUser.getUsername();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminReports(Model model) {
        model.addAttribute("reports", reportService.getAllReports());
        return "admin/reports";
    }

    @PostMapping("/admin/resolve/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String resolveReport(@PathVariable Long reportId, RedirectAttributes redirectAttributes) {
        reportService.resolveReport(reportId);
        redirectAttributes.addFlashAttribute("message", "Report resolved successfully");
        return "redirect:/report/admin";
    }
}