package com.research.analyzer.services;

import com.research.analyzer.client.ReportServiceClient;
import com.research.analyzer.dto.AnalysisSummaryResponse;
import com.research.analyzer.dto.AnalyzeRequest;
import com.research.analyzer.rule.RuleEngine;
import com.research.analyzer.servicesImpl.AnalysisServiceImpl;
import com.research.analyzer.servicesImpl.OpenApiParserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

    @Mock
    private ReportServiceClient reportServiceClient;

    private final OpenApiParserService parserService = new OpenApiParserServiceImpl();
    private final RuleEngine ruleEngine = new RuleEngine(
            java.util.Arrays.asList(
                    new com.research.analyzer.rule.impl.ResourceOrientedUriRule(),
                    new com.research.analyzer.rule.impl.PluralResourceNameRule(),
                    new com.research.analyzer.rule.impl.HttpMethodSemanticsRule(),
                    new com.research.analyzer.rule.impl.LowercasePathRule(),
                    new com.research.analyzer.rule.impl.OperationIdPresentRule(),
                    new com.research.analyzer.rule.impl.ErrorResponseDefinedRule(),
                    new com.research.analyzer.rule.impl.SuccessResponseDefinedRule(),
                    new com.research.analyzer.rule.impl.NoTrailingSlashRule(),
                    new com.research.analyzer.rule.impl.ApiVersionPresentRule(),
                    new com.research.analyzer.rule.impl.PathParameterInUriRule()
            )
    );

    @Test
    void testAnalyzeGoodApi() throws IOException {
        AnalysisService service = new AnalysisServiceImpl(parserService, ruleEngine, reportServiceClient);
        doNothing().when(reportServiceClient).saveAnalysis(any());

        AnalyzeRequest request = new AnalyzeRequest();
        request.setApiName("Good API");
        request.setSource("test");
        request.setSpecification(readResource("good-api.json"));

        AnalysisSummaryResponse response = service.analyze(request);

        assertNotNull(response.getAnalysisId());
        assertTrue(response.getTotalRules() > 0);
        assertTrue(response.getScore() >= 0);
    }

    private String readResource(String name) throws IOException {
        ClassPathResource resource = new ClassPathResource(name);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}