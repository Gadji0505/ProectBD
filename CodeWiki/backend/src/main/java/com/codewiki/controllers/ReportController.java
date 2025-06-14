package com.codewiki.controllers;

import com.codewiki.models.Report;
import com.codewiki.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public List<Report> getAllReports() {
        return reportService.findAll();
    }

    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return reportService.save(report);
    }

    @PutMapping("/{id}/resolve")
    public Report resolveReport(@PathVariable Long id) {
        return reportService.resolve(id);
    }
}