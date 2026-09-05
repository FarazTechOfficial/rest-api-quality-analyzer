package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class DeleteReturns204Rule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-014";
    }

    @Override
    public String getRuleName() {
        return "DELETE returns 204 No Content";
    }

    @Override
    public String getPracticeId() {
        return "P-5.4";
    }

    @Override
    public String getDescription() {
        return "DELETE operations should return 204 No Content on successful deletion.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        if (!"DELETE".equals(endpoint.getMethod())) {
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    true,
                    "Rule applies only to DELETE operations.",
                    null
            );
        }

        boolean has204 = false;
        for (ApiResponseInfo response : endpoint.getResponses()) {
            if ("204".equals(response.getStatusCode())) {
                has204 = true;
                break;
            }
        }

        if (!has204) {
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    false,
                    "DELETE operation does not define a 204 No Content response.",
                    "Add a 204 No Content response for successful deletion."
            );
        }

        return new RuleResultDto(
                getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                true,
                "DELETE operation defines a 204 No Content response.",
                null
        );
    }
}
