package com.research.report.service;

import com.research.report.dto.AnalysisReportResponse;
import com.research.report.dto.AnalysisSummaryDto;
import com.research.report.dto.RuleResultDto;
import com.research.report.dto.SaveAnalysisRequest;
import com.research.report.model.RuleResultStatus;
import com.research.report.entity.ApiAnalysis;
import com.research.report.entity.RuleViolation;
import com.research.report.exception.AnalysisNotFoundException;
import com.research.report.repository.ApiAnalysisRepository;
import com.research.report.repository.RuleViolationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final ApiAnalysisRepository analysisRepository;
    private final RuleViolationRepository violationRepository;

    public ReportService(ApiAnalysisRepository analysisRepository,
                         RuleViolationRepository violationRepository) {
        this.analysisRepository = analysisRepository;
        this.violationRepository = violationRepository;
    }

    @Transactional
    public void saveAnalysis(SaveAnalysisRequest request) {
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

        analysisRepository.save(analysis);

        for (RuleResultDto result : request.getResults()) {
            RuleViolation violation = new RuleViolation();
            violation.setAnalysis(analysis);
            violation.setRuleId(result.getRuleId());
            violation.setRuleName(result.getRuleName());
            violation.setPracticeId(result.getPracticeId());
            violation.setEndpoint(result.getEndpoint());
            violation.setMethod(result.getMethod());
            violation.setStatus(result.getStatus());
            violation.setPassed(result.isPassed());
            violation.setMessage(result.getMessage());
            violation.setRecommendation(result.getRecommendation());
            violationRepository.save(violation);
        }
    }

    public AnalysisReportResponse getReport(String analysisId) {
        ApiAnalysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new AnalysisNotFoundException(analysisId));

        List<RuleViolation> violations = violationRepository.findByAnalysis_Id(analysisId);

        AnalysisReportResponse response = new AnalysisReportResponse();
        response.setAnalysisId(analysis.getId());
        response.setApiName(analysis.getApiName());
        response.setVersion(analysis.getVersion());
        response.setSource(analysis.getSource());
        response.setAnalyzedAt(analysis.getAnalyzedAt());
        response.setTotalRules(analysis.getTotalRules());
        response.setPassedRules(analysis.getPassedRules());
        response.setFailedRules(analysis.getFailedRules());
        response.setSkippedRules(analysis.getSkippedRules());
        response.setScore(analysis.getScore());

        List<RuleResultDto> results = new ArrayList<>();
        for (RuleViolation violation : violations) {
            RuleResultDto dto = new RuleResultDto();
            dto.setRuleId(violation.getRuleId());
            dto.setRuleName(violation.getRuleName());
            dto.setPracticeId(violation.getPracticeId());
            dto.setEndpoint(violation.getEndpoint());
            dto.setMethod(violation.getMethod());
            dto.setStatus(violation.getStatus());
            dto.setPassed(violation.isPassed());
            dto.setMessage(violation.getMessage());
            dto.setRecommendation(violation.getRecommendation());
            results.add(dto);
        }
        response.setResults(results);

        return response;
    }

    public List<AnalysisSummaryDto> listReports(String apiName) {
        List<ApiAnalysis> analyses;

        if (apiName != null && !apiName.trim().isEmpty()) {
            analyses = analysisRepository.findByApiNameContainingIgnoreCaseOrderByAnalyzedAtDesc(apiName);
        } else {
            analyses = analysisRepository.findByOrderByAnalyzedAtDesc();
        }

        List<AnalysisSummaryDto> summaries = new ArrayList<>();
        for (ApiAnalysis analysis : analyses) {
            summaries.add(toSummary(analysis));
        }
        return summaries;
    }

    public AnalysisReportResponse getViolationsOnly(String analysisId) {
        AnalysisReportResponse fullReport = getReport(analysisId);
        List<RuleResultDto> failures = new ArrayList<>();

        for (RuleResultDto result : fullReport.getResults()) {
            if (!result.isPassed()) {
                failures.add(result);
            }
        }
        fullReport.setResults(failures);
        return fullReport;
    }

    private AnalysisSummaryDto toSummary(ApiAnalysis analysis) {
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
