package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class NoUnderscoreInUriRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-012";
    }

    @Override
    public String getRuleName() {
        return "No underscores in URI";
    }

    @Override
    public String getPracticeId() {
        return "U-4";
    }

    @Override
    public String getDescription() {
        return "URIs should use hyphens instead of underscores for word separation.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        String path = endpoint.getPath();
        String[] segments = path.split("/");

        for (String segment : segments) {
            if (segment.isEmpty() || segment.startsWith("{")) {
                continue;
            }
            if (segment.contains("_")) {
                return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                        endpoint.getPath(), endpoint.getMethod(), false,
                        "Path segment '" + segment + "' has an underscore.",
                        "Use hyphens instead, e.g. /order-items instead of /order_items.");
            }
        }

        return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                endpoint.getPath(), endpoint.getMethod(), true,
                "URI has no underscores.", null);
    }
}
