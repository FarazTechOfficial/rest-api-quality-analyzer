package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class Get404ForNotFoundRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-015";
    }

    @Override
    public String getRuleName() {
        return "GET documents 404 for not found";
    }

    @Override
    public String getPracticeId() {
        return "P-5.10";
    }

    @Override
    public String getDescription() {
        return "GET operations on individual resources should document a 404 Not Found response.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        if (!"GET".equals(endpoint.getMethod())) {
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    true,
                    "Rule applies only to GET operations.",
                    null
            );
        }

        boolean isCollection = !endpoint.getPath().matches(".*/\\{[^}]+\\}$");
        if (isCollection) {
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    true,
                    "Rule applies to individual resource GET, not collection GET.",
                    null
            );
        }

        boolean has404 = false;
        for (ApiResponseInfo response : endpoint.getResponses()) {
            if ("404".equals(response.getStatusCode())) {
                has404 = true;
                break;
            }
        }

        if (!has404) {
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    false,
                    "GET on individual resource does not document a 404 Not Found response.",
                    "Add a 404 response to indicate the resource may not exist."
            );
        }

        return new RuleResultDto(
                getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                true,
                "GET documents 404 Not Found response.",
                null
        );
    }
}
