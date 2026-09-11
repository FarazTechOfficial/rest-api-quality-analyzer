package com.research.report.servicesImpl;

import com.research.report.dto.AnalysisReportResponse;
import com.research.report.dto.AnalysisSummaryDto;
import com.research.report.dto.RuleResultDto;
import com.research.report.dto.SaveAnalysisRequest;
import com.research.report.entity.ApiAnalysis;
import com.research.report.entity.RuleViolation;
import com.research.report.exception.AnalysisNotFoundException;
import com.research.report.repository.ApiAnalysisRepository;
import com.research.report.repository.RuleViolationRepository;
import com.research.report.services.ReportService;
import com.research.report.transfer.ApiAnalysisTransformer;
import com.research.report.transfer.RuleViolationTransformer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final ApiAnalysisRepository analysisRepository;
    private final RuleViolationRepository violationRepository;
    private final ApiAnalysisTransformer analysisTransformer;
    private final RuleViolationTransformer violationTransformer;

    public ReportServiceImpl(ApiAnalysisRepository analysisRepository,
                             RuleViolationRepository violationRepository,
                             ApiAnalysisTransformer analysisTransformer,
                             RuleViolationTransformer violationTransformer) {
        this.analysisRepository = analysisRepository;
        this.violationRepository = violationRepository;
        this.analysisTransformer = analysisTransformer;
        this.violationTransformer = violationTransformer;
    }

    @Override
    @Transactional
    public void saveAnalysis(SaveAnalysisRequest request) {
        ApiAnalysis analysis = analysisTransformer.toEntity(request);
        analysisRepository.save(analysis);

        for (RuleResultDto result : request.getResults()) {
            RuleViolation violation = violationTransformer.toEntity(result, analysis);
            violationRepository.save(violation);
        }
    }

    @Override
    public AnalysisReportResponse getReport(String analysisId) {
        ApiAnalysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new AnalysisNotFoundException(analysisId));

        List<RuleViolation> violations = violationRepository.findByAnalysis_Id(analysisId);

        AnalysisReportResponse report = new AnalysisReportResponse();
        report.setAnalysisId(analysis.getId());
        report.setApiName(analysis.getApiName());
        report.setVersion(analysis.getVersion());
        report.setSource(analysis.getSource());
        report.setAnalyzedAt(analysis.getAnalyzedAt());
        report.setTotalRules(analysis.getTotalRules());
        report.setPassedRules(analysis.getPassedRules());
        report.setFailedRules(analysis.getFailedRules());
        report.setSkippedRules(analysis.getSkippedRules());
        report.setScore(analysis.getScore());

        List<RuleResultDto> results = new ArrayList<>();
        for (RuleViolation violation : violations) {
            results.add(violationTransformer.toDto(violation));
        }
        report.setResults(results);

        return report;
    }

    @Override
    public List<AnalysisSummaryDto> listReports(String apiName) {
        List<ApiAnalysis> analyses;

        if (apiName != null && !apiName.trim().isEmpty()) {
            analyses = analysisRepository.findByApiNameContainingIgnoreCaseOrderByAnalyzedAtDesc(apiName);
        } else {
            analyses = analysisRepository.findByOrderByAnalyzedAtDesc();
        }

        List<AnalysisSummaryDto> summaries = new ArrayList<>();
        for (ApiAnalysis analysis : analyses) {
            summaries.add(analysisTransformer.toSummary(analysis));
        }
        return summaries;
    }

    @Override
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
}