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
                RuleResultDto result = new RuleResultDto();
                result.setRuleId(getRuleId());
                result.setRuleName(getRuleName());
                result.setPracticeId(getPracticeId());
                result.setEndpoint(endpoint.getPath());
                result.setMethod(endpoint.getMethod());
                result.setPassed(false);
                result.setMessage("Path segment '" + segment + "' has an underscore.");
                result.setRecommendation("Use hyphens instead, e.g. /order-items instead of /order_items.");
                return result;
            }
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("URI has no underscores.");
        return result;
    }
}
