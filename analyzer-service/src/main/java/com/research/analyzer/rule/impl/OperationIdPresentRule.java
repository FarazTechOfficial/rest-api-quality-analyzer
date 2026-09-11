package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class OperationIdPresentRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-005";
    }

    @Override
    public String getRuleName() {
        return "Operation ID present";
    }

    @Override
    public String getPracticeId() {
        return "OPENAPI-01";
    }

    @Override
    public String getDescription() {
        return "Each operation should define an operationId for tooling and docs.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        String operationId = endpoint.getOperationId();

        if (operationId == null || operationId.trim().isEmpty()) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(false);
            result.setMessage("Operation is missing an operationId.");
            result.setRecommendation("Add a unique operationId to each operation in the OpenAPI spec.");
            return result;
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("Operation has an operationId.");
        return result;
    }
}
