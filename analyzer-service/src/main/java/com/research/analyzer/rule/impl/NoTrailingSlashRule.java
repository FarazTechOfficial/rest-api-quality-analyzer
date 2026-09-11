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
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(false);
            result.setMessage("URI path ends with a trailing slash.");
            result.setRecommendation("Remove the trailing slash from the path.");
            return result;
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("URI path has no trailing slash.");
        return result;
    }
}
