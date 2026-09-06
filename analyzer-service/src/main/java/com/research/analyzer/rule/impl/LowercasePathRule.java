package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class LowercasePathRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-004";
    }

    @Override
    public String getRuleName() {
        return "Lowercase path segments";
    }

    @Override
    public String getPracticeId() {
        return "U-5";
    }

    @Override
    public String getDescription() {
        return "URI path segments should use lowercase letters.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        String path = endpoint.getPath();
        String[] segments = path.split("/");

        for (String segment : segments) {
            if (segment.isEmpty() || segment.startsWith("{")) {
                continue;
            }
            if (!segment.equals(segment.toLowerCase())) {
                return new RuleResultDto(
                        getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                        false,
                        "Path segment '" + segment + "' contains uppercase characters.",
                        "Use lowercase path segments such as /order-items."
                );
            }
        }

        return new RuleResultDto(
                getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                true,
                "Path segments use lowercase.",
                null
        );
    }
}
