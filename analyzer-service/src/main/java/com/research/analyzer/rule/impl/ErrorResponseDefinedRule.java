package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class ErrorResponseDefinedRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-006";
    }

    @Override
    public String getRuleName() {
        return "At least one error response documented";
    }

    @Override
    public String getPracticeId() {
        return "OPENAPI-04";
    }

    @Override
    public String getDescription() {
        return "Operations should document at least one 4xx or 5xx error response.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        boolean hasErrorResponse = false;

        for (ApiResponseInfo response : endpoint.getResponses()) {
            if (response.isClientError() || response.isServerError()) {
                hasErrorResponse = true;
                break;
            }
        }

        if (!hasErrorResponse) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(false);
            result.setMessage("No 4xx or 5xx response is documented for this operation.");
            result.setRecommendation("Document error responses such as 400, 404, or 500.");
            return result;
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("At least one error response is documented.");
        return result;
    }
}