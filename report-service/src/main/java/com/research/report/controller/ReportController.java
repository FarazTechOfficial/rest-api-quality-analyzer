package com.research.report.controller;

import com.research.report.dto.AnalysisReportResponse;
import com.research.report.dto.AnalysisSummaryDto;
import com.research.report.dto.SaveAnalysisRequest;
import com.research.report.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<Void> saveReport(@RequestBody SaveAnalysisRequest request) {
        reportService.saveAnalysis(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisReportResponse> getReport(@PathVariable String id) {
        AnalysisReportResponse report = reportService.getReport(id);
        return ResponseEntity.ok(report);
    }

    @GetMapping
    public ResponseEntity<List<AnalysisSummaryDto>> listReports(
            @RequestParam(required = false) String apiName) {
        List<AnalysisSummaryDto> reports = reportService.listReports(apiName);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}/violations")
    public ResponseEntity<AnalysisReportResponse> getViolations(@PathVariable String id) {
        AnalysisReportResponse report = reportService.getViolationsOnly(id);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<AnalysisReportResponse> exportReport(@PathVariable String id) {
        AnalysisReportResponse report = reportService.getReport(id);
        return ResponseEntity.ok(report);
    }
}
