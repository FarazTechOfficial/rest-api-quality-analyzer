package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class NoTrailingSlashRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-008";
    }

    @Override
    public String getRuleName() {
        return "No trailing slash";
    }

    @Override
    public String getPracticeId() {
        return "U-2";
    }

    @Override
    public String getDescription() {
        return "URI paths should not end with a trailing slash.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        String path = endpoint.getPath();

        if (path.length() > 1 && path.endsWith("/")) {
            return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                    endpoint.getPath(), endpoint.getMethod(), false,
                    "URI path ends with a trailing slash.",
                    "Remove the trailing slash from the path.");
        }

        return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                endpoint.getPath(), endpoint.getMethod(), true,
                "URI path has no trailing slash.", null);
    }
}
