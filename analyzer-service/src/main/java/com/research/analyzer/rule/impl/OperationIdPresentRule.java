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
        return "Each operation should define an operationId for tooling and documentation.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        String operationId = endpoint.getOperationId();

        if (operationId == null || operationId.trim().isEmpty()) {
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    false,
                    "Operation is missing an operationId.",
                    "Add a unique operationId to each operation in the OpenAPI spec."
            );
        }

        return new RuleResultDto(
                getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                true,
                "Operation has an operationId defined.",
                null
        );
    }
}
