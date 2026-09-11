package com.research.report.services;

import com.research.report.dto.AnalysisReportResponse;
import com.research.report.dto.AnalysisSummaryDto;
import com.research.report.dto.SaveAnalysisRequest;

import java.util.List;

public interface ReportService {

    void saveAnalysis(SaveAnalysisRequest request);

    AnalysisReportResponse getReport(String analysisId);

    List<AnalysisSummaryDto> listReports(String apiName);

    AnalysisReportResponse getViolationsOnly(String analysisId);
}