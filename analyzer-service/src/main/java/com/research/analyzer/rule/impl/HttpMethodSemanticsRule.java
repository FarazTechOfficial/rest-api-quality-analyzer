package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class HttpMethodSemanticsRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-003";
    }

    @Override
    public String getRuleName() {
        return "HTTP method semantics";
    }

    @Override
    public String getPracticeId() {
        return "RM-2";
    }

    @Override
    public String getDescription() {
        return "GET, HEAD and DELETE should not define a request body.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        String method = endpoint.getMethod();
        boolean hasBody = endpoint.isHasRequestBody();

        if ("GET".equals(method) && hasBody) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(false);
            result.setMessage("GET defines a request body, which is uncommon and often unsupported.");
            result.setRecommendation("Remove the request body from GET or use POST for complex queries.");
            return result;
        }

        if ("HEAD".equals(method) && hasBody) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(false);
            result.setMessage("HEAD defines a request body.");
            result.setRecommendation("Remove the request body from HEAD.");
            return result;
        }

        if ("DELETE".equals(method) && hasBody) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(false);
            result.setMessage("DELETE defines a request body, which is uncommon.");
            result.setRecommendation("Use path or query parameters instead of a request body for DELETE.");
            return result;
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("HTTP method usage looks correct.");
        return result;
    }
}
