package com.codewiki.codewiki.controller.admin;

import com.codewiki.codewiki.model.Report;
import com.codewiki.codewiki.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final ReportService reportService;

    @Autowired
    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String listReports(Model model) {
        model.addAttribute("reports", reportService.getAllReports());
        return "admin/reports";
    }

    @PostMapping("/resolve/{id}")
    public String resolveReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reportService.resolveReport(id)
            .ifPresentOrElse(
                report -> redirectAttributes.addFlashAttribute("message", "Report resolved successfully"),
                () -> redirectAttributes.addFlashAttribute("error", "Report not found")
            );
        return "redirect:/admin/reports";
    }

    @PostMapping("/reject/{id}")
    public String rejectReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reportService.rejectReport(id)
            .ifPresentOrElse(
                report -> redirectAttributes.addFlashAttribute("message", "Report rejected successfully"),
                () -> redirectAttributes.addFlashAttribute("error", "Report not found")
            );
        return "redirect:/admin/reports";
    }

    @PostMapping("/delete/{id}")
    public String deleteReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reportService.deleteReport(id);
            redirectAttributes.addFlashAttribute("message", "Report deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting report: " + e.getMessage());
        }
        return "redirect:/admin/reports";
    }
}