package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class PostReturns201Rule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-013";
    }

    @Override
    public String getRuleName() {
        return "POST returns 201 Created";
    }

    @Override
    public String getPracticeId() {
        return "E-3";
    }

    @Override
    public String getDescription() {
        return "POST operations that create resources should return 201 Created.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        if (!"POST".equals(endpoint.getMethod())) {
            return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                    endpoint.getPath(), endpoint.getMethod(), true,
                    "Rule applies only to POST operations.", null);
        }

        boolean has201 = false;
        for (ApiResponseInfo response : endpoint.getResponses()) {
            if ("201".equals(response.getStatusCode())) {
                has201 = true;
                break;
            }
        }

        if (!has201) {
            return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                    endpoint.getPath(), endpoint.getMethod(), false,
                    "POST does not define a 201 Created response.",
                    "Add a 201 Created response for resource creation operations.");
        }

        return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                endpoint.getPath(), endpoint.getMethod(), true,
                "POST defines a 201 Created response.", null);
    }
}