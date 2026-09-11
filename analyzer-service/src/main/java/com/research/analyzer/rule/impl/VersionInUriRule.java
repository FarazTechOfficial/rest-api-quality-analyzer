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
        return "U-7";
    }

    @Override
    public String getDescription() {
        return "Versioning should be done via headers or media type, not URI paths.";
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
                return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                        endpoint.getPath(), endpoint.getMethod(), false,
                        "URI path contains a version segment '" + segment + "'.",
                        "Consider versioning via Accept header or query parameter instead of the URI.");
            }
        }

        return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                endpoint.getPath(), endpoint.getMethod(), true,
                "URI path does not embed a version.", null);
    }
}