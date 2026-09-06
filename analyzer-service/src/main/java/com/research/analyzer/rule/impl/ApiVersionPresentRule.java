package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class ApiVersionPresentRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-009";
    }

    @Override
    public String getRuleName() {
        return "API version in info block";
    }

    @Override
    public String getPracticeId() {
        return "OPENAPI-06";
    }

    @Override
    public String getDescription() {
        return "The OpenAPI info block should include a version field.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        String version = specification.getVersion();

        if (version == null || version.trim().isEmpty()) {
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    false,
                    "OpenAPI info.version is missing or empty.",
                    "Set info.version in the OpenAPI specification."
            );
        }

        return new RuleResultDto(
                getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                true,
                "OpenAPI info.version is present.",
                null
        );
    }
}
