package com.codewiki.controllers;

import com.codewiki.models.Article;
import com.codewiki.models.Report;
import com.codewiki.models.User;
import com.codewiki.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public String getStats() {
        return adminService.getDashboardStats();
    }

    @GetMapping("/reports/unresolved")
    public List<Report> getUnresolvedReports() {
        return adminService.findUnresolvedReports();
    }

    @PutMapping("/articles/{id}/approve")
    public Article approveArticle(@PathVariable Long id) {
        return adminService.approveArticle(id);
    }
}