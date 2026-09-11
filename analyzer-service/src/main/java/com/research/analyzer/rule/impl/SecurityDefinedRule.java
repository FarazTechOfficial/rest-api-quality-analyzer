package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class SecurityDefinedRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-018";
    }

    @Override
    public String getRuleName() {
        return "Security scheme defined";
    }

    @Override
    public String getPracticeId() {
        return "O-10";
    }

    @Override
    public String getDescription() {
        return "The API should define at least one security scheme.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        if (specification.getSecuritySchemes().isEmpty()) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(false);
            result.setMessage("No security schemes are defined in the API spec.");
            result.setRecommendation("Define at least one security scheme (e.g. OAuth2, API key, JWT Bearer).");
            return result;
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("Security scheme(s) defined: " + String.join(", ", specification.getSecuritySchemes()));
        return result;
    }

    @Override
    public boolean isSpecLevel() {
        return true;
    }
}