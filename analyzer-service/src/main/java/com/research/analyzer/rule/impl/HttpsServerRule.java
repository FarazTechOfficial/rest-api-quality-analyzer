package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class HttpsServerRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-016";
    }

    @Override
    public String getRuleName() {
        return "HTTPS server URL";
    }

    @Override
    public String getPracticeId() {
        return "O-14";
    }

    @Override
    public String getDescription() {
        return "API servers should use HTTPS for transport security.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        if (specification.getServerUrls().isEmpty()) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(true);
            result.setMessage("No server URLs defined; nothing to check.");
            return result;
        }

        for (String url : specification.getServerUrls()) {
            String lower = url.toLowerCase();
            int schemeEnd = lower.indexOf("://");
            if (schemeEnd <= 0) {
                continue;
            }
            String scheme = lower.substring(0, schemeEnd);
            if (!"https".equals(scheme)) {
                RuleResultDto result = new RuleResultDto();
                result.setRuleId(getRuleId());
                result.setRuleName(getRuleName());
                result.setPracticeId(getPracticeId());
                result.setEndpoint(endpoint.getPath());
                result.setMethod(endpoint.getMethod());
                result.setPassed(false);
                result.setMessage("Server URL '" + url + "' does not use HTTPS.");
                result.setRecommendation("Use HTTPS for all server URLs to ensure transport security.");
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
        result.setMessage("All server URLs use HTTPS.");
        return result;
    }

    @Override
    public boolean isSpecLevel() {
        return true;
    }
}