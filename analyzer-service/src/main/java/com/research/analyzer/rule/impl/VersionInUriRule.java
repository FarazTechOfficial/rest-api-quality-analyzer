package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class VersionInUriRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-017";
    }

    @Override
    public String getRuleName() {
        return "Version not in URI path";
    }

    @Override
    public String getPracticeId() {
        return "P-3.3";
    }

    @Override
    public String getDescription() {
        return "API versioning should be done via headers or media type, not embedded in URI paths.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        String path = endpoint.getPath();
        String[] segments = path.split("/");

        for (String segment : segments) {
            if (segment.isEmpty()) {
                continue;
            }
            if (segment.matches("v\\d+") || segment.matches("v\\d+.*")) {
                return new RuleResultDto(
                        getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                        false,
                        "URI path contains a version segment '" + segment + "'.",
                        "Consider using API versioning via Accept header or query parameter instead of embedding in the URI."
                );
            }
        }

        return new RuleResultDto(
                getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                true,
                "URI path does not embed a version.",
                null
        );
    }
}
