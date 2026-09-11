package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ResourceOrientedUriRule implements RestApiRule {

    private static final List<String> ACTION_VERBS = Arrays.asList(
            "get", "create", "update", "delete", "remove", "fetch",
            "add", "insert", "modify", "search", "find", "list"
    );

    @Override
    public String getRuleId() {
        return "REST-001";
    }

    @Override
    public String getRuleName() {
        return "Resource-oriented URI";
    }

    @Override
    public String getPracticeId() {
        return "U-12";
    }

    @Override
    public String getDescription() {
        return "URIs should name resources, not actions.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        String path = endpoint.getPath().toLowerCase();
        String[] segments = path.split("/");

        for (String segment : segments) {
            if (segment.isEmpty()) {
                continue;
            }
            String cleanSegment = segment.replaceAll("[{}]", "");
            for (String verb : ACTION_VERBS) {
                if (cleanSegment.equals(verb) || cleanSegment.startsWith(verb)) {
                    RuleResultDto result = new RuleResultDto();
                    result.setRuleId(getRuleId());
                    result.setRuleName(getRuleName());
                    result.setPracticeId(getPracticeId());
                    result.setEndpoint(endpoint.getPath());
                    result.setMethod(endpoint.getMethod());
                    result.setPassed(false);
                    result.setMessage("The URI segment '" + segment + "' looks like an action-oriented name.");
                    result.setRecommendation("Use a noun-based path such as /users instead of /getUsers.");
                    return result;
                }
            }
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("URI uses resource-oriented naming.");
        return result;
    }
}
