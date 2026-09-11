package com.research.report.transfer;

import com.research.report.dto.AnalysisSummaryDto;
import com.research.report.dto.SaveAnalysisRequest;
import com.research.report.entity.ApiAnalysis;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ApiAnalysisTransformer {

    public ApiAnalysis toEntity(SaveAnalysisRequest request) {
        ApiAnalysis analysis = new ApiAnalysis();
        analysis.setId(request.getAnalysisId());
        analysis.setApiName(request.getApiName());
        analysis.setVersion(request.getVersion());
        analysis.setSource(request.getSource());
        analysis.setAnalyzedAt(LocalDateTime.now());
        analysis.setTotalRules(request.getTotalRules());
        analysis.setPassedRules(request.getPassedRules());
        analysis.setFailedRules(request.getFailedRules());
        analysis.setSkippedRules(request.getSkippedRules());
        analysis.setScore(request.getScore());
        return analysis;
    }

    public AnalysisSummaryDto toSummary(ApiAnalysis analysis) {
        AnalysisSummaryDto summary = new AnalysisSummaryDto();
        summary.setAnalysisId(analysis.getId());
        summary.setApiName(analysis.getApiName());
        summary.setVersion(analysis.getVersion());
        summary.setSource(analysis.getSource());
        summary.setAnalyzedAt(analysis.getAnalyzedAt());
        summary.setTotalRules(analysis.getTotalRules());
        summary.setPassedRules(analysis.getPassedRules());
        summary.setFailedRules(analysis.getFailedRules());
        summary.setSkippedRules(analysis.getSkippedRules());
        summary.setScore(analysis.getScore());
        return summary;
    }
}