package com.research.analyzer.servicesImpl;

import com.research.analyzer.client.ReportServiceClient;
import com.research.analyzer.dto.AnalysisSummaryResponse;
import com.research.analyzer.dto.AnalyzeRequest;
import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.dto.SaveAnalysisRequest;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.model.RuleResultStatus;
import com.research.analyzer.rule.RuleEngine;
import com.research.analyzer.services.AnalysisService;
import com.research.analyzer.services.OpenApiParserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final OpenApiParserService parserService;
    private final RuleEngine ruleEngine;
    private final ReportServiceClient reportServiceClient;

    public AnalysisServiceImpl(OpenApiParserService parserService,
                               RuleEngine ruleEngine,
                               ReportServiceClient reportServiceClient) {
        this.parserService = parserService;
        this.ruleEngine = ruleEngine;
        this.reportServiceClient = reportServiceClient;
    }

    @Override
    public AnalysisSummaryResponse analyze(AnalyzeRequest request) {
        ApiSpecification specification = parserService.parse(request.getSpecification());
        List<RuleResultDto> results = ruleEngine.evaluateAll(specification);

        int totalRules = results.size();
        int passedRules = 0;
        int failedRules = 0;
        int skippedRules = 0;

        for (RuleResultDto result : results) {
            RuleResultStatus status = result.getStatus();
            if (status == RuleResultStatus.PASSED) {
                passedRules++;
            } else if (status == RuleResultStatus.FAILED) {
                failedRules++;
            } else {
                skippedRules++;
            }
        }

        int evaluatedRules = passedRules + failedRules;
        double score = 0.0;
        if (evaluatedRules > 0) {
            score = Math.round((passedRules * 100.0 / evaluatedRules) * 100.0) / 100.0;
        }

        String analysisId = UUID.randomUUID().toString();
        reportServiceClient.saveAnalysis(buildSaveRequest(request, specification, analysisId, totalRules, passedRules, failedRules, skippedRules, score, results));

        AnalysisSummaryResponse response = new AnalysisSummaryResponse();
        response.setAnalysisId(analysisId);
        response.setApiName(request.getApiName());
        response.setTotalRules(totalRules);
        response.setPassedRules(passedRules);
        response.setFailedRules(failedRules);
        response.setSkippedRules(skippedRules);
        response.setScore(score);
        response.setResults(results);

        return response;
    }

    private SaveAnalysisRequest buildSaveRequest(AnalyzeRequest request, ApiSpecification specification, String analysisId,
                                                 int totalRules, int passedRules, int failedRules, int skippedRules,
                                                 double score, List<RuleResultDto> results) {
        SaveAnalysisRequest saveRequest = new SaveAnalysisRequest();
        saveRequest.setAnalysisId(analysisId);
        saveRequest.setApiName(request.getApiName());
        saveRequest.setVersion(resolveVersion(request, specification));
        saveRequest.setSource(request.getSource());
        saveRequest.setTotalRules(totalRules);
        saveRequest.setPassedRules(passedRules);
        saveRequest.setFailedRules(failedRules);
        saveRequest.setSkippedRules(skippedRules);
        saveRequest.setScore(score);
        saveRequest.setResults(results);
        return saveRequest;
    }

    private String resolveVersion(AnalyzeRequest request, ApiSpecification specification) {
        if (request.getVersion() != null && !request.getVersion().trim().isEmpty()) {
            return request.getVersion();
        }
        return specification.getVersion();
    }
}