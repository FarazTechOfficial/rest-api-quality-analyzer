package com.research.analyzer.client;

import com.research.analyzer.dto.SaveAnalysisRequest;
import com.research.analyzer.exception.ReportServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ReportServiceClient {

    private final RestClient restClient;

    public ReportServiceClient(RestClient reportRestClient) {
        this.restClient = reportRestClient;
    }

    public void saveAnalysis(SaveAnalysisRequest request) {
        try {
            restClient.post()
                    .uri("/api/reports")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new ReportServiceException(
                    "Could not save the analysis to the Report Service. Make sure it is running on port 8082.");
        }
    }
}
