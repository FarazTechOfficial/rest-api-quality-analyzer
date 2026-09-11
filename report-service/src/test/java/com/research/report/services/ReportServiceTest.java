package com.research.report.services;

import com.research.report.dto.AnalysisReportResponse;
import com.research.report.dto.RuleResultDto;
import com.research.report.dto.SaveAnalysisRequest;
import com.research.report.exception.AnalysisNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Test
    void testSaveAndRetrieveReport() {
        String analysisId = UUID.randomUUID().toString();

        SaveAnalysisRequest request = new SaveAnalysisRequest();
        request.setAnalysisId(analysisId);
        request.setApiName("Test API");
        request.setVersion("1.0");
        request.setSource("unit-test");
        request.setTotalRules(2);
        request.setPassedRules(1);
        request.setFailedRules(1);
        request.setScore(50.0);

        List<RuleResultDto> results = new ArrayList<>();

        RuleResultDto pass = new RuleResultDto();
        pass.setRuleId("REST-001");
        pass.setRuleName("Test Rule Pass");
        pass.setEndpoint("/users");
        pass.setMethod("GET");
        pass.setPassed(true);
        pass.setMessage("OK");
        results.add(pass);

        RuleResultDto fail = new RuleResultDto();
        fail.setRuleId("REST-002");
        fail.setRuleName("Test Rule Fail");
        fail.setEndpoint("/getUsers");
        fail.setMethod("GET");
        fail.setPassed(false);
        fail.setMessage("Failed");
        fail.setRecommendation("Fix it");
        results.add(fail);

        request.setResults(results);

        reportService.saveAnalysis(request);

        AnalysisReportResponse report = reportService.getReport(analysisId);
        assertNotNull(report);
        assertEquals("Test API", report.getApiName());
        assertEquals(2, report.getResults().size());
    }

    @Test
    void testReportNotFound() {
        assertThrows(AnalysisNotFoundException.class,
                () -> reportService.getReport("non-existent-id"));
    }
}