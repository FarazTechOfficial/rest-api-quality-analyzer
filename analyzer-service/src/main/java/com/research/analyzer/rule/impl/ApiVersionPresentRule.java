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
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(false);
            result.setMessage("info.version is missing or empty.");
            result.setRecommendation("Set info.version in the OpenAPI spec.");
            return result;
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("info.version is present.");
        return result;
    }

    @Override
    public boolean isSpecLevel() {
        return true;
    }
}
