package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class Get404ForNotFoundRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-015";
    }

    @Override
    public String getRuleName() {
        return "GET documents 404 for not found";
    }

    @Override
    public String getPracticeId() {
        return "E-11";
    }

    @Override
    public String getDescription() {
        return "GET on a single resource should document a 404 Not Found response.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        if (!"GET".equals(endpoint.getMethod())) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(true);
            result.setMessage("Rule applies only to GET operations.");
            return result;
        }

        boolean isCollection = !endpoint.getPath().matches(".*/\\{[^}]+\\}$");
        if (isCollection) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(true);
            result.setMessage("Rule targets individual resource GET, not collection GET.");
            return result;
        }

        boolean has404 = false;
        for (ApiResponseInfo response : endpoint.getResponses()) {
            if ("404".equals(response.getStatusCode())) {
                has404 = true;
                break;
            }
        }

        if (!has404) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(false);
            result.setMessage("GET on a single resource does not document a 404 Not Found response.");
            result.setRecommendation("Add a 404 response to indicate the resource may not exist.");
            return result;
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("GET documents a 404 Not Found response.");
        return result;
    }
}